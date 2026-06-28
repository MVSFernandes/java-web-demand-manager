<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<c:set var="pageTitle" value="Comentarios" scope="request" />
<c:set var="demanda" value="${sessionScope.DemandaDetalhe}" />
<!DOCTYPE html>
<html lang="pt-BR">
<%@ include file="templates/header.jsp" %>
<body>
  <div class="app" id="app">
    <%@ include file="templates/sidebar.jsp" %>

    <div class="main">
      <%@ include file="templates/navbar.jsp" %>

      <main class="content">
        <div class="pagehead">
          <div class="pagehead__title">
            <h1>Comentarios e anexos</h1>
            <p>Historico da demanda #${demanda.idDemanda}</p>
          </div>
          <div class="pagehead__actions">
            <a class="btn btn--secondary btn--sm" href="${pageContext.request.contextPath}/views/demandas.jsp"><i data-lucide="arrow-left"></i> Voltar</a>
          </div>
        </div>

        <c:choose>
          <c:when test="${empty demanda or empty demanda.idDemanda}">
            <section class="card">
              <div class="empty">
                <i data-lucide="message-square-warning"></i>
                <h4>Nenhuma demanda selecionada</h4>
                <p>Volte para demandas e abra os comentarios de uma linha.</p>
              </div>
            </section>
          </c:when>
          <c:otherwise>
            <section class="card demanda-head">
              <div class="demanda-head__top">
                <div>
                  <span class="cell-id">#${demanda.idDemanda}</span>
                  <h2 style="margin:4px 0 6px;color:var(--slate-100);font-family:var(--font-head);">${demanda.tituloDemanda}</h2>
                  <p style="margin:0;color:var(--slate-400);font-size:13px;">${empty demanda.descricaoDemanda ? 'Sem descricao.' : demanda.descricaoDemanda}</p>
                </div>
                <span class="badge badge--${demanda.prioridadeCss}">${demanda.prioridadeTexto}</span>
              </div>
              <div class="filters" style="padding:12px 0 0;border-bottom:0;">
                <span class="pill"><span class="dot" style="background:var(--blue)"></span>${empty demanda.nomeStatus ? '-' : demanda.nomeStatus}</span>
                <span class="pill">${empty demanda.nomeSetorDestino ? '-' : demanda.nomeSetorDestino}</span>
                <span class="pill">${empty demanda.nomeCategoria ? '-' : demanda.nomeCategoria}</span>
                <span class="pill">Solicitante: ${empty demanda.nomeSolicitante ? '-' : demanda.nomeSolicitante}</span>
              </div>
            </section>

            <div class="detail-grid">
              <section class="card">
                <div class="card__head">
                  <h3>Timeline</h3>
                  <span class="filters__count"><b>${fn:length(sessionScope.ListaEventosDemanda)}</b> eventos</span>
                </div>

                <div class="timeline">
                  <c:forEach items="${sessionScope.ListaEventosDemanda}" var="evt">
                    <article class="feed-item ${param.comentario == evt.idComentario ? 'is-highlight' : ''}" id="${evt.tipo}-${empty evt.idComentario ? evt.idAnexo : evt.idComentario}">
                      <div class="event-marker" style="color:${evt.cor}"><i data-lucide="${evt.icone}"></i></div>
                      <div class="feed-item__body">
                        <p><b>${evt.autor}</b> <c:if test="${evt.tipo eq 'anexo'}">anexou um arquivo</c:if></p>
                        <p>${evt.titulo}</p>
                        <span class="feed-item__time">${evt.detalhe} - ${evt.dataFormatada}</span>
                      </div>
                      <c:if test="${evt.tipo eq 'comentario' and (sessionScope.UsuarioLogado.idUsuario == evt.idAutor or sessionScope.UsuarioLogado.perfilUsuario eq 'admin')}">
                        <span class="row-actions" style="opacity:1;">
                          <a class="is-danger js-confirm" href="${pageContext.request.contextPath}/ServletComentario?acao=excluir&id=${evt.idComentario}&idDemanda=${demanda.idDemanda}" data-msg="Excluir este comentario?" title="Excluir"><i data-lucide="trash-2"></i></a>
                        </span>
                      </c:if>
                    </article>
                  </c:forEach>
                  <c:if test="${empty sessionScope.ListaEventosDemanda}">
                    <div class="empty">
                      <i data-lucide="messages-square"></i>
                      <h4>Nenhum evento ainda</h4>
                      <p>Registre um comentario ou envie um anexo para iniciar o historico.</p>
                    </div>
                  </c:if>
                </div>
              </section>

              <aside style="display:flex;flex-direction:column;gap:16px;">
                <section class="card">
                  <div class="card__head"><h3>Novo comentario</h3></div>
                  <div class="card__body">
                    <form action="${pageContext.request.contextPath}/ServletComentario" method="POST">
                      <input type="hidden" name="idDemanda" value="${demanda.idDemanda}" />
                      <div class="field">
                        <label for="txtMensagem">Mensagem <span class="req">*</span></label>
                        <textarea class="textarea" id="txtMensagem" name="txtMensagem" placeholder="Digite seu comentario..." required></textarea>
                      </div>
                      <button type="submit" class="btn btn--primary"><i data-lucide="send"></i> Enviar</button>
                    </form>
                  </div>
                </section>

                <section class="card">
                  <div class="card__head"><h3>Anexos</h3></div>
                  <div class="card__body">
                    <form action="${pageContext.request.contextPath}/ServletAnexo" method="POST" enctype="multipart/form-data">
                      <input type="hidden" name="idDemanda" value="${demanda.idDemanda}" />
                      <label class="dropzone" for="arquivo" id="arquivoDropzone">
                        <i data-lucide="upload-cloud"></i>
                        <span><b id="arquivoNome">Selecionar arquivo</b><small>PDF, imagem, planilha ou documento ate 10 MB</small></span>
                      </label>
                      <input id="arquivo" name="arquivo" type="file" accept=".pdf,.png,.jpg,.jpeg,.gif,.webp,.doc,.docx,.xls,.xlsx,.csv,.txt,.zip" required style="position:absolute;left:-9999px;" />
                      <button type="submit" class="btn btn--secondary" id="btnEnviarAnexo" style="width:100%;margin-top:10px;" disabled><i data-lucide="paperclip"></i> Enviar anexo</button>
                    </form>

                    <div class="attach-list" style="margin-top:14px;">
                      <c:forEach items="${sessionScope.ListaAnexos}" var="anx">
                        <div class="attach-item ${param.anexo == anx.idAnexo ? 'is-highlight' : ''}" id="anexo-${anx.idAnexo}">
                          <span class="attach-ico attach-ico--${anx.tipoClasse}"><i data-lucide="${anx.icone}"></i></span>
                          <div class="attach-info">
                            <strong title="${anx.nomeArquivoAnexo}">${anx.nomeArquivoAnexo}</strong>
                            <span>${anx.tipoArquivoTexto} - ${anx.tamanhoFormatado}</span>
                            <small>Enviado por ${empty anx.nomeUsuarioUpload ? 'upload' : anx.nomeUsuarioUpload} - ${anx.criadoEmRelativo}</small>
                          </div>
                          <div class="attach-actions">
                            <c:if test="${anx.previewDisponivel}">
                              <a href="${pageContext.request.contextPath}/ServletDownload?id=${anx.idAnexo}&modo=preview" class="js-preview" data-type="${anx.previewTipo}" data-name="${anx.nomeArquivoAnexo}" title="Visualizar"><i data-lucide="eye"></i></a>
                            </c:if>
                            <a href="${pageContext.request.contextPath}/ServletDownload?id=${anx.idAnexo}" title="Baixar"><i data-lucide="download"></i></a>
                            <c:if test="${sessionScope.UsuarioLogado.idUsuario == anx.idUsuarioUploadAnexo or sessionScope.UsuarioLogado.perfilUsuario eq 'admin'}">
                              <a class="is-danger js-confirm" href="${pageContext.request.contextPath}/ServletAnexo?acao=excluir&id=${anx.idAnexo}&idDemanda=${demanda.idDemanda}" data-msg="Excluir o arquivo ${anx.nomeArquivoAnexo}?" title="Excluir"><i data-lucide="trash-2"></i></a>
                            </c:if>
                          </div>
                        </div>
                      </c:forEach>
                      <c:if test="${empty sessionScope.ListaAnexos}">
                        <div class="empty" style="padding:24px 10px;">
                          <i data-lucide="paperclip"></i>
                          <p>Nenhum anexo ainda.</p>
                        </div>
                      </c:if>
                    </div>
                  </div>
                </section>
              </aside>
            </div>
          </c:otherwise>
        </c:choose>
      </main>
    </div>

    <div class="scrim" id="scrim"></div>
  </div>

  <div class="preview-modal" id="previewModal" hidden="hidden">
    <div class="preview-modal__scrim" data-preview-close></div>
    <div class="preview-modal__panel">
      <div class="preview-modal__head">
        <strong id="previewTitle">Visualizar anexo</strong>
        <button class="icon-btn" type="button" data-preview-close><i data-lucide="x"></i></button>
      </div>
      <div class="preview-modal__body" id="previewBody"></div>
    </div>
  </div>

  <div class="toast-stack" id="toastStack"></div>
  <%@ include file="templates/javascript.jsp" %>
  <script>
    document.querySelectorAll(".js-confirm").forEach(function (link) {
      link.addEventListener("click", function (event) {
        if (!confirm(link.dataset.msg || "Confirmar acao?")) event.preventDefault();
      });
    });

    var arquivo = document.getElementById("arquivo");
    var arquivoNome = document.getElementById("arquivoNome");
    var btnEnviarAnexo = document.getElementById("btnEnviarAnexo");
    if (arquivo && arquivoNome && btnEnviarAnexo) {
      arquivo.addEventListener("change", function () {
        var file = arquivo.files && arquivo.files[0];
        if (file && file.size > 10 * 1024 * 1024) {
          arquivo.value = "";
          arquivoNome.textContent = "Selecionar arquivo";
          btnEnviarAnexo.disabled = true;
          if (window.GDI && window.GDI.toast) window.GDI.toast("Arquivo acima de 10 MB.", "erro");
          return;
        }
        arquivoNome.textContent = file ? file.name : "Selecionar arquivo";
        btnEnviarAnexo.disabled = !file;
      });
    }

    var focoEvento = document.querySelector(".attach-item.is-highlight, .feed-item.is-highlight");
    if (focoEvento) {
      focoEvento.scrollIntoView({ behavior: "smooth", block: "center" });
    }

    var previewModal = document.getElementById("previewModal");
    var previewBody = document.getElementById("previewBody");
    var previewTitle = document.getElementById("previewTitle");
    document.querySelectorAll(".js-preview").forEach(function (link) {
      link.addEventListener("click", function (event) {
        event.preventDefault();
        var type = link.dataset.type;
        var url = link.getAttribute("href");
        previewTitle.textContent = link.dataset.name || "Visualizar anexo";
        previewBody.innerHTML = type === "image"
          ? '<img src="' + url + '" alt="Anexo" />'
          : '<iframe src="' + url + '" title="Preview do anexo"></iframe>';
        previewModal.removeAttribute("hidden");
        document.body.style.overflow = "hidden";
      });
    });
    document.querySelectorAll("[data-preview-close]").forEach(function (el) {
      el.addEventListener("click", function () {
        previewModal.setAttribute("hidden", "hidden");
        previewBody.innerHTML = "";
        document.body.style.overflow = "";
      });
    });
  </script>
</body>
</html>
