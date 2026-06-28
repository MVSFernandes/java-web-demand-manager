<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<c:set var="pageTitle" value="Demandas" scope="request" />
<c:set var="drawerAberto" value="${param.nova eq '1' or not empty sessionScope.Demanda}" />
<fmt:formatDate value="${sessionScope.Demanda.slaDataLimiteDemanda}" pattern="yyyy-MM-dd" var="slaEdicao" />
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
            <h1>Demandas</h1>
            <p>Acompanhe e gerencie todas as demandas internas</p>
          </div>
          <div class="pagehead__actions">
            <button class="btn btn--primary btn--sm" id="btnNova" type="button"><i data-lucide="plus"></i> Nova demanda</button>
          </div>
        </div>

        <section class="card">
          <div class="filters">
            <label class="filters__search">
              <i data-lucide="search"></i>
              <input type="text" id="fBusca" value="${param.q}" placeholder="Buscar por titulo, setor, categoria..." />
            </label>
            <div class="filter-select">
              <select id="fStatus">
                <option value="">Todos os status</option>
                <c:forEach items="${sessionScope.ListaStatus}" var="st">
                  <option value="${empty st.nomeStatus ? '' : fn:toLowerCase(st.nomeStatus)}">${st.nomeStatus}</option>
                </c:forEach>
              </select>
            </div>
            <div class="filter-select">
              <select id="fSetor">
                <option value="">Todos os setores</option>
                <c:forEach items="${sessionScope.ListaSetores}" var="s">
                  <option value="${empty s.nomeSetor ? '' : fn:toLowerCase(s.nomeSetor)}">${s.nomeSetor}</option>
                </c:forEach>
              </select>
            </div>
            <div class="filter-select">
              <select id="fPrio">
                <option value="">Toda prioridade</option>
                <option value="alta">Alta</option>
                <option value="media">Media</option>
                <option value="baixa">Baixa</option>
              </select>
            </div>
            <button class="chip-clear" id="fClear" type="button" style="display:none"><i data-lucide="x"></i> Limpar</button>
            <div class="filters__spacer"></div>
            <span class="filters__count"><b id="cVisiveis">${fn:length(sessionScope.ListaDemandas)}</b> de <b id="cTotal">${fn:length(sessionScope.ListaDemandas)}</b></span>
          </div>

          <div class="table-wrap">
            <table class="tbl">
              <thead>
                <tr>
                  <th style="width:60px">#</th>
                  <th>Demanda</th>
                  <th>Prioridade</th>
                  <th>Setor</th>
                  <th>Categoria</th>
                  <th>Status</th>
                  <th>SLA</th>
                  <th>Solicitante</th>
                  <th style="width:96px"></th>
                </tr>
              </thead>
              <tbody id="tbodyDemandas">
                <c:forEach items="${sessionScope.ListaDemandas}" var="demanda">
                  <tr data-search="#${demanda.idDemanda} ${empty demanda.tituloDemanda ? '' : fn:toLowerCase(demanda.tituloDemanda)} ${empty demanda.nomeStatus ? '' : fn:toLowerCase(demanda.nomeStatus)} ${empty demanda.nomeSetorDestino ? '' : fn:toLowerCase(demanda.nomeSetorDestino)} ${empty demanda.nomeCategoria ? '' : fn:toLowerCase(demanda.nomeCategoria)} ${empty demanda.nomeSolicitante ? '' : fn:toLowerCase(demanda.nomeSolicitante)}" data-status="${empty demanda.nomeStatus ? '' : fn:toLowerCase(demanda.nomeStatus)}" data-setor="${empty demanda.nomeSetorDestino ? '' : fn:toLowerCase(demanda.nomeSetorDestino)}" data-prio="${demanda.prioridadeCss}">
                    <td class="cell-id">#${demanda.idDemanda}</td>
                    <td class="cell-title">${demanda.tituloDemanda}</td>
                    <td><span class="badge badge--${demanda.prioridadeCss}">${demanda.prioridadeTexto}</span></td>
                    <td>${empty demanda.nomeSetorDestino ? '-' : demanda.nomeSetorDestino}</td>
                    <td class="muted">${empty demanda.nomeCategoria ? '-' : demanda.nomeCategoria}</td>
                    <td><span class="pill"><span class="dot" style="background:var(--blue)"></span>${empty demanda.nomeStatus ? '-' : demanda.nomeStatus}</span></td>
                    <td>
                      <c:choose>
                        <c:when test="${demanda.slaStatus eq 'sem-sla'}"><span class="muted">-</span></c:when>
                        <c:otherwise><span class="sla sla--${demanda.slaStatus}"><i data-lucide="${demanda.slaIcone}"></i>${demanda.slaTexto}</span></c:otherwise>
                      </c:choose>
                    </td>
                    <td>${empty demanda.nomeSolicitante ? '-' : demanda.nomeSolicitante}</td>
                    <td class="cell-actions">
                      <span class="row-actions">
                        <a href="${pageContext.request.contextPath}/ServletDemanda?acao=comentarios&id=${demanda.idDemanda}" title="Comentarios"><i data-lucide="message-square"></i></a>
                        <a href="${pageContext.request.contextPath}/ServletDemanda?acao=carregar&id=${demanda.idDemanda}" title="Editar"><i data-lucide="pencil"></i></a>
                        <a class="is-danger js-confirm" href="${pageContext.request.contextPath}/ServletDemanda?acao=excluir&id=${demanda.idDemanda}" data-msg="Excluir a demanda #${demanda.idDemanda}?" title="Excluir"><i data-lucide="trash-2"></i></a>
                      </span>
                    </td>
                  </tr>
                </c:forEach>
              </tbody>
            </table>
            <div class="empty" id="empty" style="${empty sessionScope.ListaDemandas ? '' : 'display:none'}">
              <i data-lucide="search-x"></i>
              <h4>Nenhuma demanda encontrada</h4>
              <p>Ajuste os filtros ou crie uma nova demanda.</p>
            </div>
          </div>
        </section>
      </main>
    </div>

    <div class="scrim" id="scrim"></div>
  </div>

  <div class="drawer ${drawerAberto ? 'is-open' : ''}" id="drawer">
    <div class="drawer__scrim" data-close></div>
    <div class="drawer__panel">
      <div class="drawer__head">
        <div>
          <h2>${not empty sessionScope.Demanda ? 'Editar demanda' : 'Nova demanda'}</h2>
          <p>Preencha os dados e salve para registrar</p>
        </div>
        <button class="icon-btn" data-close title="Fechar" type="button"><i data-lucide="x"></i></button>
      </div>

      <form id="formDemanda" action="${pageContext.request.contextPath}/ServletDemanda" method="POST">
        <div class="drawer__body">
          <input type="hidden" name="id" value="${sessionScope.Demanda.idDemanda}" />

          <div class="field">
            <label for="txtTitulo">Titulo <span class="req">*</span></label>
            <input class="input" type="text" id="txtTitulo" name="txtTitulo" value="${sessionScope.Demanda.tituloDemanda}" placeholder="Ex.: Aprovacao de reembolso" required />
          </div>

          <div class="field">
            <label for="txtDescricao">Descricao</label>
            <textarea class="textarea" id="txtDescricao" name="txtDescricao" placeholder="Detalhe a demanda...">${sessionScope.Demanda.descricaoDemanda}</textarea>
          </div>

          <div class="field">
            <label>Prioridade <span class="req">*</span></label>
            <div class="segmented">
              <input type="radio" id="pAlta" name="txtPrioridade" value="1" ${sessionScope.Demanda.prioridadeDemanda == 1 ? 'checked' : ''} />
              <label for="pAlta" class="seg--alta">Alta</label>
              <input type="radio" id="pMedia" name="txtPrioridade" value="2" ${empty sessionScope.Demanda or sessionScope.Demanda.prioridadeDemanda == 2 ? 'checked' : ''} />
              <label for="pMedia" class="seg--media">Media</label>
              <input type="radio" id="pBaixa" name="txtPrioridade" value="3" ${sessionScope.Demanda.prioridadeDemanda == 3 ? 'checked' : ''} />
              <label for="pBaixa" class="seg--baixa">Baixa</label>
            </div>
          </div>

          <div class="field-row">
            <div class="field">
              <label for="txtSla">Prazo SLA</label>
              <input class="input" type="date" id="txtSla" name="txtSla" value="${slaEdicao}" />
            </div>
            <div class="field">
              <label for="idSetorDestino">Setor destino</label>
              <div class="select-wrap">
                <select class="select" id="idSetorDestino" name="idSetorDestino">
                  <option value="">Selecione...</option>
                  <c:forEach items="${sessionScope.ListaSetores}" var="s">
                    <option value="${s.idSetor}" ${sessionScope.Demanda.idSetorDestinoDemanda == s.idSetor ? 'selected' : ''}>${s.nomeSetor}</option>
                  </c:forEach>
                </select>
              </div>
            </div>
          </div>

          <div class="field-row">
            <div class="field">
              <label for="idCategoria">Categoria</label>
              <div class="select-wrap">
                <select class="select" id="idCategoria" name="idCategoria">
                  <option value="">Selecione...</option>
                  <c:forEach items="${sessionScope.ListaCategorias}" var="c">
                    <option value="${c.idCategoria}" ${sessionScope.Demanda.idCategoriaDemanda == c.idCategoria ? 'selected' : ''}>${c.nomeCategoria}</option>
                  </c:forEach>
                </select>
              </div>
            </div>

            <c:choose>
              <c:when test="${sessionScope.UsuarioLogado.perfilUsuario ne 'usuario'}">
                <div class="field">
                  <label for="idStatus">Status</label>
                  <div class="select-wrap">
                    <select class="select" id="idStatus" name="idStatus">
                      <c:forEach items="${sessionScope.ListaStatus}" var="st">
                        <option value="${st.idStatus}" ${sessionScope.Demanda.idStatusDemanda == st.idStatus ? 'selected' : ''}>${st.nomeStatus}</option>
                      </c:forEach>
                    </select>
                  </div>
                  <span class="field-hint">Visivel apenas para gestores</span>
                </div>
              </c:when>
              <c:otherwise>
                <input type="hidden" name="idStatus" value="${empty sessionScope.Demanda.idStatusDemanda ? 1 : sessionScope.Demanda.idStatusDemanda}" />
              </c:otherwise>
            </c:choose>
          </div>
        </div>

        <div class="drawer__foot">
          <button type="button" class="btn btn--secondary" data-close>Cancelar</button>
          <button type="submit" class="btn btn--primary"><i data-lucide="check"></i> Salvar demanda</button>
        </div>
      </form>
    </div>
  </div>

  <div class="toast-stack" id="toastStack"></div>
  <%@ include file="templates/javascript.jsp" %>
  <script>
    (function () {
      var drawer = document.getElementById("drawer");
      var btnNova = document.getElementById("btnNova");
      function openDrawer() { drawer.classList.add("is-open"); document.body.style.overflow = "hidden"; }
      function closeDrawer() { drawer.classList.remove("is-open"); document.body.style.overflow = ""; }
      if (drawer.classList.contains("is-open")) document.body.style.overflow = "hidden";
      if (btnNova) btnNova.addEventListener("click", openDrawer);
      window.GDI = window.GDI || {};
      window.GDI.openDemandaDrawer = openDrawer;
      drawer.querySelectorAll("[data-close]").forEach(function (el) { el.addEventListener("click", closeDrawer); });

      document.querySelectorAll(".js-confirm").forEach(function (link) {
        link.addEventListener("click", function (event) {
          if (!confirm(link.dataset.msg || "Confirmar acao?")) event.preventDefault();
        });
      });

      var busca = document.getElementById("fBusca");
      var status = document.getElementById("fStatus");
      var setor = document.getElementById("fSetor");
      var prio = document.getElementById("fPrio");
      var clear = document.getElementById("fClear");
      var rows = Array.prototype.slice.call(document.querySelectorAll("#tbodyDemandas tr"));
      var empty = document.getElementById("empty");
      var count = document.getElementById("cVisiveis");
      function filter() {
        var q = (busca.value || "").toLowerCase();
        var st = status.value;
        var se = setor.value;
        var pr = prio.value;
        var visible = 0;
        rows.forEach(function (row) {
          var ok = (!q || (row.dataset.search || "").indexOf(q) >= 0) &&
                   (!st || row.dataset.status === st) &&
                   (!se || row.dataset.setor === se) &&
                   (!pr || row.dataset.prio === pr);
          row.style.display = ok ? "" : "none";
          if (ok) visible++;
        });
        count.textContent = visible;
        empty.style.display = visible ? "none" : "";
        clear.style.display = (q || st || se || pr) ? "inline-flex" : "none";
      }
      [busca, status, setor, prio].forEach(function (el) { el.addEventListener(el.tagName === "INPUT" ? "input" : "change", filter); });
      clear.addEventListener("click", function () { busca.value = ""; status.value = ""; setor.value = ""; prio.value = ""; filter(); });
      filter();
    })();
  </script>
  <c:remove var="Demanda" scope="session"/>
</body>
</html>
