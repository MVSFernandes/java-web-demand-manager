



(function () {
  "use strict";
  var icons = window.GDIicons || function () {};

  


  var HISTORICO = [
    { tipo:"status", autor:"Sistema", iniciais:"", texto:'Demanda criada por <b>Marina Alves</b>', tempo:"há 2 dias" },
    { tipo:"comentario", autor:"Marina Alves", iniciais:"MA", texto:"Pessoal, anexei a planilha consolidada e os comprovantes. Qualquer dúvida sobre as despesas de representação, me avisem.", tempo:"há 2 dias" },
    { tipo:"event", autor:"Carlos Mendes", iniciais:"CM", texto:'<b>Carlos Mendes</b> assumiu como responsável', tempo:"há 1 dia" },
    { tipo:"status", autor:"Carlos Mendes", iniciais:"CM", texto:'Status alterado de <b>Aberta</b> → <b>Em análise</b>', tempo:"há 1 dia" },
    { tipo:"comentario", autor:"Carlos Mendes", iniciais:"CM", texto:"Revisei a planilha. Falta o comprovante do item 7 (hospedagem). Marina, consegue anexar? Sem ele não consigo aprovar o lote.", tempo:"há 5 h" },
    { tipo:"comentario", autor:"Ana Paula", iniciais:"AP", texto:"Do ponto de vista jurídico não há ressalvas nos contratos de representação. Liberado pelo nosso lado.", tempo:"há 3 h" }
  ];

  var TL_ICON = { event:"user-plus", status:"git-pull-request-arrow" };
  var TL_CLASS = { event:"tl-marker--event", status:"tl-marker--status" };

  function itemHTML(it) {
    if (it.tipo === "comentario") {
      return '<div class="tl-item">' +
        '<div class="tl-rail"><div class="tl-avatar">' + it.iniciais + '</div></div>' +
        '<div class="tl-body">' +
          '<div class="tl-meta"><strong>' + it.autor + '</strong><span class="tl-time">' + it.tempo + '</span></div>' +
          '<div class="tl-comment">' + it.texto + '</div>' +
        '</div></div>';
    }
    
    return '<div class="tl-item">' +
      '<div class="tl-rail"><div class="tl-marker ' + (TL_CLASS[it.tipo]||"") + '"><i data-lucide="' + (TL_ICON[it.tipo]||"activity") + '"></i></div></div>' +
      '<div class="tl-body" style="padding-top:6px">' +
        '<div class="tl-event-text">' + it.texto + ' <span class="tl-time">· ' + it.tempo + '</span></div>' +
      '</div></div>';
  }

  document.getElementById("timeline").innerHTML = HISTORICO.map(itemHTML).join("");

  
  var ANEXOS = [
    { nome:"reembolsos-Q2-consolidado.pdf", tam:"248 KB", tipo:"pdf" },
    { nome:"comprovante-viagem-031.jpg",     tam:"1.2 MB", tipo:"img" },
    { nome:"notas-fiscais.zip",              tam:"3.7 MB", tipo:"zip" },
    { nome:"politica-reembolso.docx",        tam:"86 KB",  tipo:"doc" }
  ];
  var ANX_ICON = { pdf:"file-text", img:"image", zip:"file-archive", doc:"file-text" };

  function anexoHTML(a, i) {
    return '<div class="attach-item">' +
      '<div class="attach-ico attach-ico--' + a.tipo + '"><i data-lucide="' + ANX_ICON[a.tipo] + '"></i></div>' +
      '<div class="attach-info"><strong>' + a.nome + '</strong><span>' + a.tam + '</span></div>' +
      '<div class="attach-actions">' +
        '<a href="#" title="Baixar"><i data-lucide="download"></i></a>' +
        '<a href="#" class="is-danger" title="Excluir" data-del="' + i + '"><i data-lucide="trash-2"></i></a>' +
      '</div></div>';
  }

  function renderAnexos() {
    var list = document.getElementById("attachList");
    list.innerHTML = ANEXOS.map(anexoHTML).join("");
    document.getElementById("anexoCount").textContent = ANEXOS.length;
    list.querySelectorAll("[data-del]").forEach(function (b) {
      b.addEventListener("click", function (e) {
        e.preventDefault();
        var i = parseInt(b.getAttribute("data-del"), 10);
        var nome = ANEXOS[i].nome;
        ANEXOS.splice(i, 1);
        renderAnexos();
        window.GDI.toast("Anexo “" + nome + "” removido.", "ok");
      });
    });
    icons();
  }
  renderAnexos();

  
  var TIPO_POR_EXT = function (nome) {
    var e = nome.split(".").pop().toLowerCase();
    if (e === "pdf") return "pdf";
    if (["png","jpg","jpeg","gif","webp","svg"].indexOf(e) > -1) return "img";
    if (["zip","rar","7z","tar","gz"].indexOf(e) > -1) return "zip";
    return "doc";
  };
  function humanSize(bytes) {
    if (bytes < 1024) return bytes + " B";
    if (bytes < 1048576) return (bytes / 1024).toFixed(0) + " KB";
    return (bytes / 1048576).toFixed(1) + " MB";
  }
  function addFiles(files) {
    Array.prototype.forEach.call(files, function (f) {
      ANEXOS.push({ nome:f.name, tam:humanSize(f.size), tipo:TIPO_POR_EXT(f.name) });
    });
    renderAnexos();
    window.GDI.toast(files.length + " anexo(s) adicionado(s).", "ok");
  }

  var dz = document.getElementById("dropzone");
  var fileInput = document.getElementById("fileInput");
  dz.addEventListener("click", function () { fileInput.click(); });
  fileInput.addEventListener("change", function () { if (fileInput.files.length) addFiles(fileInput.files); fileInput.value = ""; });
  ["dragenter","dragover"].forEach(function (ev) {
    dz.addEventListener(ev, function (e) { e.preventDefault(); dz.classList.add("is-drag"); });
  });
  ["dragleave","drop"].forEach(function (ev) {
    dz.addEventListener(ev, function (e) { e.preventDefault(); dz.classList.remove("is-drag"); });
  });
  dz.addEventListener("drop", function (e) { if (e.dataTransfer.files.length) addFiles(e.dataTransfer.files); });

  
  var form = document.getElementById("formComentario");
  var ta = document.getElementById("txtMensagem");
  form.addEventListener("submit", function (e) {
    e.preventDefault(); 
    var msg = ta.value.trim();
    if (!msg) { ta.focus(); return; }
    var tl = document.getElementById("timeline");
    tl.insertAdjacentHTML("beforeend",
      '<div class="tl-item">' +
        '<div class="tl-rail"><div class="tl-avatar">MA</div></div>' +
        '<div class="tl-body"><div class="tl-meta"><strong>Marina Alves</strong><span class="tl-time">agora</span></div>' +
        '<div class="tl-comment"></div></div></div>');
    tl.lastElementChild.querySelector(".tl-comment").textContent = msg;
    ta.value = "";
    icons();
    window.GDI.toast("Comentário publicado.", "ok");
  });
  
  ta.addEventListener("keydown", function (e) {
    if ((e.metaKey || e.ctrlKey) && e.key === "Enter") form.requestSubmit();
  });

  icons();
})();
