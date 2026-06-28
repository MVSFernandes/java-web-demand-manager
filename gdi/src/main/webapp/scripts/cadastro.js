








(function () {
  "use strict";
  var icons = window.GDIicons || function () {};
  var toast = (window.GDI && window.GDI.toast) || function (m) { alert(m); };

  function $(id) { return document.getElementById(id); }

  window.GDICrud = {
    init: function (cfg) {
      var data = cfg.data;
      var idKey = cfg.idKey || "id";
      var tbody = $(cfg.tbody || "tbody");
      var empty = $(cfg.empty || "empty");
      var countEl = $(cfg.count || "count");
      var drawer = $(cfg.drawer || "drawer");
      var form = $(cfg.form || "form");
      var drawerTitle = $(cfg.drawerTitle || "drawerTitle");
      var entity = cfg.entity || "registro";

      var api = { rerender: render, data: data, toast: toast };

      function render() {
        if (countEl) countEl.textContent = data.length;
        if (!data.length) {
          tbody.innerHTML = "";
          if (empty) empty.style.display = "block";
          icons();
          return;
        }
        if (empty) empty.style.display = "none";
        tbody.innerHTML = data.map(function (item) {
          var cls = (cfg.isInactive && cfg.isInactive(item)) ? ' class="is-inactive"' : "";
          var extra = cfg.extraActions ? cfg.extraActions(item, api) : "";
          var actions =
            '<td class="cell-actions"><span class="row-actions">' + extra +
              '<a href="#" title="Editar" data-edit="' + item[idKey] + '"><i data-lucide="pencil"></i></a>' +
              '<a href="#" class="is-danger" title="Excluir" data-del="' + item[idKey] + '"><i data-lucide="trash-2"></i></a>' +
            '</span></td>';
          return "<tr" + cls + ' data-id="' + item[idKey] + '">' + cfg.renderRow(item, api) + actions + "</tr>";
        }).join("");
        bind();
        icons();
        if (cfg.afterRender) cfg.afterRender(api);
      }

      function find(id) {
        return data.filter(function (x) { return String(x[idKey]) === String(id); })[0];
      }

      function bind() {
        tbody.querySelectorAll("[data-edit]").forEach(function (a) {
          a.addEventListener("click", function (e) { e.preventDefault(); openEdit(find(a.getAttribute("data-edit"))); });
        });
        tbody.querySelectorAll("[data-del]").forEach(function (a) {
          a.addEventListener("click", function (e) { e.preventDefault(); doDelete(find(a.getAttribute("data-del"))); });
        });
        
        tbody.querySelectorAll("[data-act]").forEach(function (b) {
          b.addEventListener("click", function (e) {
            e.preventDefault();
            var item = find(b.closest("tr").getAttribute("data-id"));
            if (cfg.onAct) cfg.onAct(b.getAttribute("data-act"), item, api);
          });
        });
      }

      function doDelete(item) {
        if (!item) return;
        
        var bloqueio = cfg.canDelete ? cfg.canDelete(item) : null;
        if (bloqueio) { toast(bloqueio, "erro"); return; }
        var nome = cfg.label ? cfg.label(item) : ("#" + item[idKey]);
        if (!confirm("Excluir " + entity + " “" + nome + "”? Esta ação não pode ser desfeita.")) return;
        
        var i = data.indexOf(item);
        if (i > -1) data.splice(i, 1);
        render();
        toast(entity.charAt(0).toUpperCase() + entity.slice(1) + " excluído com sucesso.", "ok");
      }

      
      function openNew() {
        drawerTitle.textContent = cfg.titleNew || ("Novo " + entity);
        form.reset();
        if (cfg.resetForm) cfg.resetForm(form, api);
        show();
      }
      function openEdit(item) {
        if (!item) return;
        drawerTitle.textContent = cfg.titleEdit ? cfg.titleEdit(item) : ("Editar " + entity);
        form.reset();
        if (cfg.fillForm) cfg.fillForm(form, item, api);
        show();
      }
      function show() {
        drawer.classList.add("is-open");
        document.body.style.overflow = "hidden";
        var first = form.querySelector("input:not([type=hidden]), select, textarea");
        setTimeout(function () { if (first) first.focus(); }, 320);
      }
      function close() { drawer.classList.remove("is-open"); document.body.style.overflow = ""; }

      [cfg.btnNew || "btnNovo", cfg.btnNew2 || "btnNovo2"].forEach(function (id) {
        var b = $(id); if (b) b.addEventListener("click", openNew);
      });
      drawer.querySelectorAll("[data-close]").forEach(function (b) { b.addEventListener("click", close); });
      document.addEventListener("keydown", function (e) { if (e.key === "Escape") close(); });

      form.addEventListener("submit", function (e) {
        e.preventDefault(); 
        if (cfg.validate && !cfg.validate(form)) return;
        close();
        toast(cfg.saveMsg || (entity.charAt(0).toUpperCase() + entity.slice(1) + " salvo com sucesso."), "ok");
        if (cfg.onSave) cfg.onSave(form, api);
      });

      
      api.openNew = openNew;
      api.openEdit = openEdit;
      api.close = close;
      api.find = find;

      render();
      cfg.api = api;
      return api;
    }
  };
})();
