<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<c:set var="pageTitle" value="Setores" scope="request" />
<c:set var="drawerAberto" value="${param.novo eq '1' or not empty sessionScope.Setor}" />
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
          <div class="pagehead__title"><h1>Setores</h1><p>Organize areas internas e seus responsaveis</p></div>
          <div class="pagehead__actions"><button class="btn btn--primary btn--sm" id="btnNovo" type="button"><i data-lucide="plus"></i> Novo setor</button></div>
        </div>

        <section class="card">
          <div class="filters">
            <label class="filters__search"><i data-lucide="search"></i><input type="text" id="fBusca" placeholder="Buscar setor..." /></label>
            <div class="filters__spacer"></div>
            <span class="filters__count"><b id="cVisiveis">${fn:length(sessionScope.ListaSetores)}</b> de <b>${fn:length(sessionScope.ListaSetores)}</b></span>
          </div>
          <div class="table-wrap">
            <table class="tbl">
              <thead><tr><th style="width:60px">#</th><th>Nome</th><th>Descricao</th><th>Gerente</th><th style="width:96px"></th></tr></thead>
              <tbody id="tbodySetores">
                <c:forEach items="${sessionScope.ListaSetores}" var="s">
                  <tr data-title="${empty s.nomeSetor ? '' : fn:toLowerCase(s.nomeSetor)} ${empty s.descricaoSetor ? '' : fn:toLowerCase(s.descricaoSetor)}">
                    <td class="cell-id">#${s.idSetor}</td>
                    <td class="cell-title">${s.nomeSetor}</td>
                    <td>${empty s.descricaoSetor ? '-' : s.descricaoSetor}</td>
                    <td>${empty s.nomeGerente ? '-' : s.nomeGerente}</td>
                    <td class="cell-actions">
                      <span class="row-actions">
                        <a href="${pageContext.request.contextPath}/ServletSetor?acao=carregar&id=${s.idSetor}" title="Editar"><i data-lucide="pencil"></i></a>
                        <a class="is-danger js-confirm" href="${pageContext.request.contextPath}/ServletSetor?acao=excluir&id=${s.idSetor}" data-msg="Excluir o setor ${s.nomeSetor}?" title="Excluir"><i data-lucide="trash-2"></i></a>
                      </span>
                    </td>
                  </tr>
                </c:forEach>
              </tbody>
            </table>
            <div class="empty" id="empty" style="${empty sessionScope.ListaSetores ? '' : 'display:none'}"><i data-lucide="network"></i><h4>Nenhum setor encontrado</h4><p>Cadastre setores para classificar usuarios e demandas.</p></div>
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
        <div><h2>${not empty sessionScope.Setor ? 'Editar setor' : 'Novo setor'}</h2><p>Informe nome, descricao e gerente responsavel</p></div>
        <button class="icon-btn" data-close type="button" title="Fechar"><i data-lucide="x"></i></button>
      </div>
      <form action="${pageContext.request.contextPath}/ServletSetor" method="POST">
        <div class="drawer__body">
          <input type="hidden" name="id" value="${sessionScope.Setor.idSetor}" />
          <div class="field"><label for="txtNome">Nome <span class="req">*</span></label><input class="input" id="txtNome" name="txtNome" value="${sessionScope.Setor.nomeSetor}" required /></div>
          <div class="field"><label for="txtDescricao">Descricao</label><textarea class="textarea" id="txtDescricao" name="txtDescricao">${sessionScope.Setor.descricaoSetor}</textarea></div>
          <div class="field">
            <label for="idGerenteSetor">Gerente</label>
            <div class="select-wrap">
              <select class="select" id="idGerenteSetor" name="idGerenteSetor">
                <option value="">Sem gerente definido</option>
                <c:forEach items="${sessionScope.ListaGerentes}" var="g">
                  <option value="${g.idUsuario}" ${sessionScope.Setor.idGerenteSetor == g.idUsuario ? 'selected' : ''}>${g.nomeUsuario}</option>
                </c:forEach>
              </select>
            </div>
            <span class="field-hint">O campo sera salvo quando o script B2 do banco estiver aplicado.</span>
          </div>
        </div>
        <div class="drawer__foot">
          <button class="btn btn--secondary" type="button" data-close>Cancelar</button>
          <button class="btn btn--primary" type="submit"><i data-lucide="save"></i> Salvar setor</button>
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
      var busca = document.getElementById("fBusca");
      var rows = Array.prototype.slice.call(document.querySelectorAll("#tbodySetores tr"));
      var empty = document.getElementById("empty");
      var count = document.getElementById("cVisiveis");
      busca.addEventListener("input", function () {
        var q = busca.value.toLowerCase();
        var visible = 0;
        rows.forEach(function (row) {
          var ok = !q || row.dataset.title.indexOf(q) >= 0;
          row.style.display = ok ? "" : "none";
          if (ok) visible++;
        });
        count.textContent = visible;
        empty.style.display = visible ? "none" : "";
      });
    })();
  </script>
  <c:remove var="Setor" scope="session"/>
</body>
</html>
