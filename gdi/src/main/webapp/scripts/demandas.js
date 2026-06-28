



(function () {
  "use strict";
  var icons = window.GDIicons || function () {};

  
  var DEMANDAS = [
    { id:1058, titulo:"Aprovação de reembolso Q2",        prioridade:"alta",  setor:"Financeiro",       categoria:"Financeiro",        status:"Em análise",  sla:"vencido", slaTxt:"-2d", solicitante:"Marina Alves" },
    { id:1057, titulo:"Provisionar VM ambiente homolog.", prioridade:"media", setor:"Infraestrutura",   categoria:"Suporte técnico",   status:"Em andamento",sla:"urgente", slaTxt:"6h",  solicitante:"Carlos Mendes" },
    { id:1056, titulo:"Revisar contrato fornecedor TI",   prioridade:"alta",  setor:"Jurídico",         categoria:"Compra / aquisição",status:"Aberta",      sla:"ok",      slaTxt:"3d",  solicitante:"Ana Paula" },
    { id:1055, titulo:"Onboarding novo analista RH",      prioridade:"baixa", setor:"Recursos Humanos", categoria:"Recursos humanos",  status:"Em andamento",sla:"ok",      slaTxt:"5d",  solicitante:"Diego Rocha" },
    { id:1054, titulo:"Ajuste de acesso ao ERP",          prioridade:"media", setor:"TI",               categoria:"Solicitação de acesso",status:"Em análise", sla:"urgente", slaTxt:"9h",  solicitante:"Beatriz Lima" },
    { id:1053, titulo:"Compra de licenças design",        prioridade:"baixa", setor:"Marketing",        categoria:"Compra / aquisição",status:"Aberta",      sla:"ok",      slaTxt:"8d",  solicitante:"Rafael Souza" },
    { id:1052, titulo:"Falha no ponto eletrônico",        prioridade:"alta",  setor:"Recursos Humanos", categoria:"Suporte técnico",   status:"Em andamento",sla:"vencido", slaTxt:"-1d", solicitante:"Diego Rocha" },
    { id:1051, titulo:"Atualizar política de senhas",     prioridade:"media", setor:"TI",               categoria:"Suporte técnico",   status:"Concluída",   sla:"ok",      slaTxt:"—",   solicitante:"Beatriz Lima" },
    { id:1050, titulo:"Renovação de certificado SSL",     prioridade:"alta",  setor:"Infraestrutura",   categoria:"Suporte técnico",   status:"Em análise",  sla:"urgente", slaTxt:"4h",  solicitante:"Carlos Mendes" },
    { id:1049, titulo:"Solicitação de notebook novo",     prioridade:"baixa", setor:"TI",               categoria:"Compra / aquisição",status:"Aberta",      sla:"ok",      slaTxt:"10d", solicitante:"Marina Alves" },
    { id:1048, titulo:"Conciliação bancária mensal",      prioridade:"media", setor:"Financeiro",       categoria:"Financeiro",        status:"Concluída",   sla:"ok",      slaTxt:"—",   solicitante:"Ana Paula" },
    { id:1047, titulo:"Parecer jurídico LGPD",            prioridade:"alta",  setor:"Jurídico",         categoria:"Suporte técnico",   status:"Em andamento",sla:"urgente", slaTxt:"7h",  solicitante:"Rafael Souza" },
    { id:1046, titulo:"Treinamento de segurança",         prioridade:"baixa", setor:"Recursos Humanos", categoria:"Recursos humanos",  status:"Aberta",      sla:"ok",      slaTxt:"12d", solicitante:"Diego Rocha" },
    { id:1045, titulo:"Migração de servidor de e-mail",   prioridade:"alta",  setor:"Infraestrutura",   categoria:"Suporte técnico",   status:"Em análise",  sla:"vencido", slaTxt:"-3d", solicitante:"Carlos Mendes" },
    { id:1044, titulo:"Campanha institucional Q3",        prioridade:"media", setor:"Marketing",        categoria:"Compra / aquisição",status:"Em andamento",sla:"ok",      slaTxt:"6d",  solicitante:"Beatriz Lima" },
    { id:1043, titulo:"Acesso ao painel financeiro",      prioridade:"media", setor:"Financeiro",       categoria:"Solicitação de acesso",status:"Concluída",sla:"ok",     slaTxt:"—",   solicitante:"Marina Alves" },
    { id:1042, titulo:"Backup do banco de produção",      prioridade:"alta",  setor:"Infraestrutura",   categoria:"Suporte técnico",   status:"Concluída",   sla:"ok",      slaTxt:"—",   solicitante:"Carlos Mendes" },
    { id:1041, titulo:"Atualização de organograma",       prioridade:"baixa", setor:"Recursos Humanos", categoria:"Recursos humanos",  status:"Aberta",      sla:"ok",      slaTxt:"9d",  solicitante:"Diego Rocha" },
    { id:1040, titulo:"Integração API de pagamentos",     prioridade:"alta",  setor:"TI",               categoria:"Suporte técnico",   status:"Em andamento",sla:"urgente", slaTxt:"5h",  solicitante:"Rafael Souza" },
    { id:1039, titulo:"Auditoria de licenças software",   prioridade:"media", setor:"Jurídico",         categoria:"Compra / aquisição",status:"Em análise",  sla:"ok",      slaTxt:"4d",  solicitante:"Ana Paula" }
  ];

  var PRIO = { alta:"Alta", media:"Média", baixa:"Baixa" };
  var SLA_ICON = { vencido:"alarm-clock-off", urgente:"alarm-clock", ok:"clock" };
  var STATUS_COR = { "Aberta":"var(--slate-400)", "Em análise":"var(--amber)", "Em andamento":"var(--blue)", "Concluída":"var(--green)", "Cancelada":"var(--red)" };
  var PAGE_SIZE = 10;

  var state = { busca:"", status:"", setor:"", prio:"", page:1 };

  var $ = function (id) { return document.getElementById(id); };
  var tbody = $("tbody");

  function filtered() {
    return DEMANDAS.filter(function (d) {
      if (state.busca && d.titulo.toLowerCase().indexOf(state.busca.toLowerCase()) < 0) return false;
      if (state.status && d.status !== state.status) return false;
      if (state.setor && d.setor !== state.setor) return false;
      if (state.prio && d.prioridade !== state.prio) return false;
      return true;
    });
  }

  function rowHTML(d) {
    var sla = (d.sla === "ok" && d.slaTxt === "—")
      ? '<span class="muted">—</span>'
      : '<span class="sla sla--' + d.sla + '"><i data-lucide="' + SLA_ICON[d.sla] + '"></i>' + d.slaTxt + '</span>';
    return '<tr>' +
      '<td class="cell-id">#' + d.id + '</td>' +
      '<td class="cell-title">' + d.titulo + '</td>' +
      '<td><span class="badge badge--' + d.prioridade + '">' + PRIO[d.prioridade] + '</span></td>' +
      '<td>' + d.setor + '</td>' +
      '<td class="muted">' + d.categoria + '</td>' +
      '<td><span class="pill"><span class="dot" style="background:' + (STATUS_COR[d.status]||'var(--slate-400)') + '"></span>' + d.status + '</span></td>' +
      '<td>' + sla + '</td>' +
      '<td>' + d.solicitante + '</td>' +
      '<td class="cell-actions"><span class="row-actions">' +
        
        '<a href="comentarios.html" title="Comentários"><i data-lucide="message-square"></i></a>' +
        
        '<a href="#" title="Editar" data-edit="' + d.id + '"><i data-lucide="pencil"></i></a>' +
        
        '<a href="#" class="is-danger" title="Excluir" data-del="' + d.id + '"><i data-lucide="trash-2"></i></a>' +
      '</span></td>' +
    '</tr>';
  }

  function render() {
    var list = filtered();
    var total = list.length;
    var pages = Math.max(1, Math.ceil(total / PAGE_SIZE));
    if (state.page > pages) state.page = pages;
    var start = (state.page - 1) * PAGE_SIZE;
    var slice = list.slice(start, start + PAGE_SIZE);

    tbody.innerHTML = slice.map(rowHTML).join("");
    $("empty").style.display = total === 0 ? "block" : "none";

    $("cVisiveis").textContent = total;
    $("cTotal").textContent = DEMANDAS.length;
    $("pFrom").textContent = total ? (start + 1) : 0;
    $("pTo").textContent = Math.min(start + PAGE_SIZE, total);
    $("pTotal").textContent = total;

    var anyFilter = state.busca || state.status || state.setor || state.prio;
    $("fClear").style.display = anyFilter ? "inline-flex" : "none";

    renderPager(pages);
    bindRowActions();
    icons();
  }

  function renderPager(pages) {
    var pager = $("pager");
    var html = '<button ' + (state.page === 1 ? "disabled" : "") + ' data-go="' + (state.page - 1) + '"><i data-lucide="chevron-left"></i></button>';
    var nums = [];
    for (var i = 1; i <= pages; i++) {
      if (i === 1 || i === pages || Math.abs(i - state.page) <= 1) nums.push(i);
      else if (nums[nums.length - 1] !== "…") nums.push("…");
    }
    nums.forEach(function (n) {
      if (n === "…") html += '<span class="pager__ellipsis">…</span>';
      else html += '<button class="' + (n === state.page ? "is-active" : "") + '" data-go="' + n + '">' + n + '</button>';
    });
    html += '<button ' + (state.page === pages ? "disabled" : "") + ' data-go="' + (state.page + 1) + '"><i data-lucide="chevron-right"></i></button>';
    pager.innerHTML = html;
    pager.querySelectorAll("button[data-go]").forEach(function (b) {
      b.addEventListener("click", function () { state.page = parseInt(b.getAttribute("data-go"), 10); render(); });
    });
  }

  function bindRowActions() {
    tbody.querySelectorAll("[data-edit]").forEach(function (a) {
      a.addEventListener("click", function (e) { e.preventDefault(); openDrawer(parseInt(a.getAttribute("data-edit"), 10)); });
    });
    tbody.querySelectorAll("[data-del]").forEach(function (a) {
      a.addEventListener("click", function (e) {
        e.preventDefault();
        var id = a.getAttribute("data-del");
        if (confirm("Excluir a demanda #" + id + "? Esta ação não pode ser desfeita.")) {
          
          var i = DEMANDAS.findIndex(function (d) { return d.id == id; });
          if (i > -1) DEMANDAS.splice(i, 1);
          render();
          window.GDI.toast("Demanda #" + id + " excluída.", "ok");
        }
      });
    });
  }

  
  function onFilter() {
    state.busca = $("fBusca").value;
    state.status = $("fStatus").value;
    state.setor = $("fSetor").value;
    state.prio = $("fPrio").value;
    state.page = 1;
    render();
  }
  $("fBusca").addEventListener("input", onFilter);
  $("fStatus").addEventListener("change", onFilter);
  $("fSetor").addEventListener("change", onFilter);
  $("fPrio").addEventListener("change", onFilter);
  $("fClear").addEventListener("click", function () {
    $("fBusca").value = ""; $("fStatus").value = ""; $("fSetor").value = ""; $("fPrio").value = "";
    onFilter();
  });

  
  var drawer = $("drawer");
  var form = $("formDemanda");
  function openDrawer(id) {
    if (id) {
      var d = DEMANDAS.find(function (x) { return x.id === id; });
      $("drawerTitle").textContent = "Editar demanda #" + id;
      form.elements["idDemanda"].value = id;
      $("txtTitulo").value = d.titulo;
      $("txtDescricao").value = "";
      var prioVal = PRIO[d.prioridade];
      form.querySelectorAll('[name="txtPrioridade"]').forEach(function (r) { r.checked = (r.value === prioVal); });
    } else {
      $("drawerTitle").textContent = "Nova demanda";
      form.reset();
      form.elements["idDemanda"].value = "";
    }
    drawer.classList.add("is-open");
    document.body.style.overflow = "hidden";
    setTimeout(function () { $("txtTitulo").focus(); }, 320);
  }
  function closeDrawer() { drawer.classList.remove("is-open"); document.body.style.overflow = ""; }

  $("btnNova").addEventListener("click", function () { openDrawer(); });
  $("btnNova2").addEventListener("click", function () { openDrawer(); });
  drawer.querySelectorAll("[data-close]").forEach(function (b) { b.addEventListener("click", closeDrawer); });
  document.addEventListener("keydown", function (e) { if (e.key === "Escape") closeDrawer(); });

  form.addEventListener("submit", function (e) {
    e.preventDefault(); 
    var titulo = $("txtTitulo").value.trim();
    if (!titulo) return;
    closeDrawer();
    window.GDI.toast("Demanda salva com sucesso.", "ok");
  });

  render();
})();
