



(function () {
  "use strict";

  function renderIcons() {
    if (window.lucide && typeof window.lucide.createIcons === "function") window.lucide.createIcons();
  }
  window.GDIicons = renderIcons;

  
  var app = document.getElementById("app");
  var btn = document.getElementById("btnSidebar");
  var scrim = document.getElementById("scrim");
  var COLLAPSE_KEY = "gdi.sidebar.collapsed";
  var mobile = function () { return window.matchMedia("(max-width: 760px)").matches; };

  if (app && localStorage.getItem(COLLAPSE_KEY) === "1") app.classList.add("is-collapsed");

  if (btn && app) {
    btn.addEventListener("click", function () {
      if (mobile()) {
        app.classList.toggle("is-mobile-open");
      } else {
        app.classList.toggle("is-collapsed");
        localStorage.setItem(COLLAPSE_KEY, app.classList.contains("is-collapsed") ? "1" : "0");
      }
    });
  }
  if (scrim && app) scrim.addEventListener("click", function () { app.classList.remove("is-mobile-open"); });
  document.addEventListener("keydown", function (e) {
    if (e.key === "Escape" && app) app.classList.remove("is-mobile-open");
    if (e.key === "/" && document.activeElement.tagName !== "INPUT" && document.activeElement.tagName !== "TEXTAREA") {
      var s = document.querySelector(".search input, .filters__search input");
      if (s) { e.preventDefault(); s.focus(); }
    }
  });

  
  var ICONS = {
    ok:   '<i data-lucide="check-circle-2"></i>',
    erro: '<i data-lucide="alert-octagon"></i>',
    info: '<i data-lucide="info"></i>'
  };
  function toast(msg, tipo) {
    tipo = tipo || "info";
    var stack = document.getElementById("toastStack");
    if (!stack) { stack = document.createElement("div"); stack.id = "toastStack"; stack.className = "toast-stack"; document.body.appendChild(stack); }
    var el = document.createElement("div");
    el.className = "toast toast--" + tipo;
    el.innerHTML =
      '<span class="toast__icon">' + (ICONS[tipo] || ICONS.info) + '</span>' +
      '<div class="toast__body"><strong>' +
        (tipo === "erro" ? "Erro" : tipo === "ok" ? "Tudo certo" : "Aviso") +
      '</strong><p></p></div>' +
      '<button class="toast__close" aria-label="Fechar"><i data-lucide="x"></i></button>';
    el.querySelector(".toast__body p").textContent = msg;
    stack.appendChild(el);
    renderIcons();
    var killed = false;
    function dismiss() { if (killed) return; killed = true; el.classList.add("is-leaving"); setTimeout(function () { el.remove(); }, 280); }
    el.querySelector(".toast__close").addEventListener("click", dismiss);
    setTimeout(dismiss, 4200);
  }
  window.GDI = window.GDI || {};
  window.GDI.toast = toast;

  function ensureConfirmModal() {
    var modal = document.getElementById("gdiConfirmModal");
    if (modal) return modal;
    modal = document.createElement("div");
    modal.className = "confirm-modal";
    modal.id = "gdiConfirmModal";
    modal.setAttribute("hidden", "hidden");
    modal.innerHTML =
      '<div class="confirm-modal__scrim" data-confirm-cancel></div>' +
      '<section class="confirm-modal__panel" role="dialog" aria-modal="true" aria-labelledby="gdiConfirmTitle">' +
        '<div class="confirm-modal__icon"><i data-lucide="trash-2"></i></div>' +
        '<div class="confirm-modal__content">' +
          '<strong id="gdiConfirmTitle">Confirmar acao</strong>' +
          '<p id="gdiConfirmMessage">Deseja continuar?</p>' +
        '</div>' +
        '<div class="confirm-modal__actions">' +
          '<button class="btn btn--secondary btn--sm" type="button" data-confirm-cancel>Cancelar</button>' +
          '<button class="btn btn--danger btn--sm" type="button" data-confirm-ok>Excluir</button>' +
        '</div>' +
      '</section>';
    document.body.appendChild(modal);
    renderIcons();
    return modal;
  }

  function openConfirm(message, href) {
    var modal = ensureConfirmModal();
    var ok = modal.querySelector("[data-confirm-ok]");
    var msg = modal.querySelector("#gdiConfirmMessage");
    msg.textContent = message || "Deseja continuar?";
    ok.textContent = /^Excluir/i.test(message || "") ? "Excluir" : "Confirmar";
    modal.removeAttribute("hidden");
    document.body.style.overflow = "hidden";

    function close() {
      modal.setAttribute("hidden", "hidden");
      document.body.style.overflow = "";
      modal.querySelectorAll("[data-confirm-cancel]").forEach(function (btn) {
        btn.removeEventListener("click", close);
      });
      ok.removeEventListener("click", confirm);
      document.removeEventListener("keydown", onKey);
    }

    function confirm() {
      close();
      window.location.href = href;
    }

    function onKey(event) {
      if (event.key === "Escape") close();
    }

    modal.querySelectorAll("[data-confirm-cancel]").forEach(function (btn) {
      btn.addEventListener("click", close);
    });
    ok.addEventListener("click", confirm);
    document.addEventListener("keydown", onKey);
  }

  document.addEventListener("click", function (event) {
    var link = event.target.closest(".js-confirm");
    if (!link) return;
    event.preventDefault();
    event.stopPropagation();
    event.stopImmediatePropagation();
    openConfirm(link.dataset.msg || "Confirmar acao?", link.getAttribute("href"));
  }, true);

  renderIcons();
})();
