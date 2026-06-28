<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<c:set var="pageTitle" value="Categorias" scope="request" />
<c:set var="drawerAberto" value="${param.novo eq '1' or not empty sessionScope.Categoria}" />
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
          <div class="pagehead__title"><h1>Categorias</h1><p>Agrupe demandas por tipo de solicitacao</p></div>
          <div class="pagehead__actions"><button class="btn btn--primary btn--sm" id="btnNovo" type="button"><i data-lucide="plus"></i> Nova categoria</button></div>
        </div>
        <section class="card">
          <div class="filters">
            <label class="filters__search"><i data-lucide="search"></i><input type="text" id="fBusca" placeholder="Buscar categoria..." /></label>
            <div class="filters__spacer"></div>
            <span class="filters__count"><b id="cVisiveis">${fn:length(sessionScope.ListaCategorias)}</b> de <b>${fn:length(sessionScope.ListaCategorias)}</b></span>
          </div>
          <div class="table-wrap">
            <table class="tbl">
              <thead><tr><th style="width:60px">#</th><th>Nome</th><th>Descricao</th><th style="width:96px"></th></tr></thead>
              <tbody id="tbodyCategorias">
                <c:forEach items="${sessionScope.ListaCategorias}" var="c">
                  <tr data-title="${empty c.nomeCategoria ? '' : fn:toLowerCase(c.nomeCategoria)} ${empty c.descricaoCategoria ? '' : fn:toLowerCase(c.descricaoCategoria)}">
                    <td class="cell-id">#${c.idCategoria}</td>
                    <td class="cell-title">${c.nomeCategoria}</td>
                    <td>${empty c.descricaoCategoria ? '-' : c.descricaoCategoria}</td>
                    <td class="cell-actions">
                      <span class="row-actions">
                        <a href="${pageContext.request.contextPath}/ServletCategoria?acao=carregar&id=${c.idCategoria}" title="Editar"><i data-lucide="pencil"></i></a>
                        <a class="is-danger js-confirm" href="${pageContext.request.contextPath}/ServletCategoria?acao=excluir&id=${c.idCategoria}" data-msg="Excluir a categoria ${c.nomeCategoria}?" title="Excluir"><i data-lucide="trash-2"></i></a>
                      </span>
                    </td>
                  </tr>
                </c:forEach>
              </tbody>
            </table>
            <div class="empty" id="empty" style="${empty sessionScope.ListaCategorias ? '' : 'display:none'}"><i data-lucide="tags"></i><h4>Nenhuma categoria encontrada</h4><p>Cadastre categorias para organizar demandas.</p></div>
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
        <div><h2>${not empty sessionScope.Categoria ? 'Editar categoria' : 'Nova categoria'}</h2><p>Defina nome e descricao</p></div>
        <button class="icon-btn" data-close type="button" title="Fechar"><i data-lucide="x"></i></button>
      </div>
      <form action="${pageContext.request.contextPath}/ServletCategoria" method="POST">
        <div class="drawer__body">
          <input type="hidden" name="id" value="${sessionScope.Categoria.idCategoria}" />
          <div class="field"><label for="txtNome">Nome <span class="req">*</span></label><input class="input" id="txtNome" name="txtNome" value="${sessionScope.Categoria.nomeCategoria}" required /></div>
          <div class="field"><label for="txtDescricao">Descricao</label><textarea class="textarea" id="txtDescricao" name="txtDescricao">${sessionScope.Categoria.descricaoCategoria}</textarea></div>
        </div>
        <div class="drawer__foot">
          <button class="btn btn--secondary" type="button" data-close>Cancelar</button>
          <button class="btn btn--primary" type="submit"><i data-lucide="save"></i> Salvar categoria</button>
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
      var busca = document.getElementById("fBusca"), rows = Array.prototype.slice.call(document.querySelectorAll("#tbodyCategorias tr")), empty = document.getElementById("empty"), count = document.getElementById("cVisiveis");
      busca.addEventListener("input", function () { var q = busca.value.toLowerCase(), visible = 0; rows.forEach(function (row) { var ok = !q || row.dataset.title.indexOf(q) >= 0; row.style.display = ok ? "" : "none"; if (ok) visible++; }); count.textContent = visible; empty.style.display = visible ? "none" : ""; });
    })();
  </script>
  <c:remove var="Categoria" scope="session"/>
</body>
</html>
