<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<c:set var="pageTitle" value="Status" scope="request" />
<c:set var="drawerAberto" value="${param.novo eq '1' or not empty sessionScope.Status}" />
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
          <div class="pagehead__title"><h1>Status</h1><p>Controle as etapas do fluxo de atendimento</p></div>
          <div class="pagehead__actions"><button class="btn btn--primary btn--sm" id="btnNovo" type="button"><i data-lucide="plus"></i> Novo status</button></div>
        </div>
        <section class="card">
          <div class="filters">
            <label class="filters__search"><i data-lucide="search"></i><input type="text" id="fBusca" placeholder="Buscar status..." /></label>
            <div class="filters__spacer"></div>
            <span class="filters__count"><b id="cVisiveis">${fn:length(sessionScope.ListaStatus)}</b> de <b>${fn:length(sessionScope.ListaStatus)}</b></span>
          </div>
          <div class="table-wrap">
            <table class="tbl">
              <thead><tr><th style="width:60px">#</th><th>Nome</th><th>Ordem</th><th>Preview</th><th style="width:96px"></th></tr></thead>
              <tbody id="tbodyStatus">
                <c:forEach items="${sessionScope.ListaStatus}" var="st">
                  <tr data-title="${empty st.nomeStatus ? '' : fn:toLowerCase(st.nomeStatus)}">
                    <td class="cell-id">#${st.idStatus}</td>
                    <td class="cell-title">${st.nomeStatus}</td>
                    <td>${st.ordemStatus}</td>
                    <td><span class="pill"><span class="dot" style="background:var(--accent)"></span>${st.nomeStatus}</span></td>
                    <td class="cell-actions">
                      <span class="row-actions">
                        <a href="${pageContext.request.contextPath}/ServletStatus?acao=carregar&id=${st.idStatus}" title="Editar"><i data-lucide="pencil"></i></a>
                        <a class="is-danger js-confirm" href="${pageContext.request.contextPath}/ServletStatus?acao=excluir&id=${st.idStatus}" data-msg="Excluir o status ${st.nomeStatus}?" title="Excluir"><i data-lucide="trash-2"></i></a>
                      </span>
                    </td>
                  </tr>
                </c:forEach>
              </tbody>
            </table>
            <div class="empty" id="empty" style="${empty sessionScope.ListaStatus ? '' : 'display:none'}"><i data-lucide="git-pull-request-arrow"></i><h4>Nenhum status encontrado</h4><p>Cadastre etapas para o fluxo de demandas.</p></div>
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
        <div><h2>${not empty sessionScope.Status ? 'Editar status' : 'Novo status'}</h2><p>Defina nome e ordem no fluxo</p></div>
        <button class="icon-btn" data-close type="button" title="Fechar"><i data-lucide="x"></i></button>
      </div>
      <form action="${pageContext.request.contextPath}/ServletStatus" method="POST">
        <div class="drawer__body">
          <input type="hidden" name="id" value="${sessionScope.Status.idStatus}" />
          <div class="field"><label for="txtNome">Nome <span class="req">*</span></label><input class="input" id="txtNome" name="txtNome" value="${sessionScope.Status.nomeStatus}" required /></div>
          <div class="field"><label for="txtOrdem">Ordem</label><input class="input" id="txtOrdem" name="txtOrdem" type="number" min="1" value="${sessionScope.Status.ordemStatus}" /></div>
        </div>
        <div class="drawer__foot">
          <button class="btn btn--secondary" type="button" data-close>Cancelar</button>
          <button class="btn btn--primary" type="submit"><i data-lucide="save"></i> Salvar status</button>
        </div>
      </form>
    </div>
  </div>
  <div class="toast-stack" id="toastStack"></div>
  <%@ include file="templates/javascript.jsp" %>
  <script>
    (function () {
      var drawer = document.getElementById("drawer");
      function openDrawer(){ drawer.classList.add("is-open"); document.body.style.overflow = "hidden"; }
      function closeDrawer(){ drawer.classList.remove("is-open"); document.body.style.overflow = ""; }
      if (drawer.classList.contains("is-open")) document.body.style.overflow = "hidden";
      document.getElementById("btnNovo").addEventListener("click", openDrawer);
      drawer.querySelectorAll("[data-close]").forEach(function (el) { el.addEventListener("click", closeDrawer); });
      document.querySelectorAll(".js-confirm").forEach(function (link) { link.addEventListener("click", function (event) { if (!confirm(link.dataset.msg || "Confirmar acao?")) event.preventDefault(); }); });
      var busca = document.getElementById("fBusca"), rows = Array.prototype.slice.call(document.querySelectorAll("#tbodyStatus tr")), empty = document.getElementById("empty"), count = document.getElementById("cVisiveis");
      busca.addEventListener("input", function () { var q = busca.value.toLowerCase(), visible = 0; rows.forEach(function (row) { var ok = !q || row.dataset.title.indexOf(q) >= 0; row.style.display = ok ? "" : "none"; if (ok) visible++; }); count.textContent = visible; empty.style.display = visible ? "none" : ""; });
    })();
  </script>
  <c:remove var="Status" scope="session"/>
</body>
</html>
