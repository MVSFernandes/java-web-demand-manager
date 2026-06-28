<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<c:set var="pageTitle" value="Usuarios" scope="request" />
<c:set var="drawerAberto" value="${param.novo eq '1' or not empty sessionScope.User}" />
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
            <h1>Usuarios</h1>
            <p>Gerencie acessos, perfis e setores do sistema</p>
          </div>
          <div class="pagehead__actions">
            <button class="btn btn--primary btn--sm" id="btnNovo" type="button"><i data-lucide="plus"></i> Novo usuario</button>
          </div>
        </div>

        <section class="card">
          <div class="filters">
            <label class="filters__search"><i data-lucide="search"></i><input type="text" id="fBusca" placeholder="Buscar usuario..." /></label>
            <div class="filters__spacer"></div>
            <span class="filters__count"><b id="cVisiveis">${fn:length(sessionScope.ListaUsuarios)}</b> de <b>${fn:length(sessionScope.ListaUsuarios)}</b></span>
          </div>
          <div class="table-wrap">
            <table class="tbl">
              <thead>
                <tr><th style="width:60px">#</th><th>Nome</th><th>E-mail</th><th>Perfil</th><th>Status</th><th>Setor</th><th style="width:116px"></th></tr>
              </thead>
              <tbody id="tbodyUsuarios">
                <c:forEach items="${sessionScope.ListaUsuarios}" var="u">
                  <tr class="${u.ativoUsuario == 0 ? 'is-inactive' : ''}" data-title="${empty u.nomeUsuario ? '' : fn:toLowerCase(u.nomeUsuario)} ${empty u.emailUsuario ? '' : fn:toLowerCase(u.emailUsuario)}">
                    <td class="cell-id">#${u.idUsuario}</td>
                    <td class="cell-title">${u.nomeUsuario}</td>
                    <td>${u.emailUsuario}</td>
                    <td><span class="tag tag--${u.perfilUsuario}">${u.perfilUsuario}</span></td>
                    <td>
                      <c:choose>
                        <c:when test="${u.ativoUsuario == 1}"><span class="tag tag--ativo">Ativo</span></c:when>
                        <c:otherwise><span class="tag tag--inativo">Inativo</span></c:otherwise>
                      </c:choose>
                    </td>
                    <td>${empty u.nomeSetor ? '-' : u.nomeSetor}</td>
                    <td class="cell-actions">
                      <span class="row-actions">
                        <a href="${pageContext.request.contextPath}/ServletUsuario?acao=carregar&id=${u.idUsuario}" title="Editar"><i data-lucide="pencil"></i></a>
                        <c:choose>
                          <c:when test="${u.ativoUsuario == 1}">
                            <a class="is-danger js-confirm" href="${pageContext.request.contextPath}/ServletUsuario?acao=desativar&id=${u.idUsuario}" data-msg="Desativar ${u.nomeUsuario}?" title="Desativar"><i data-lucide="user-x"></i></a>
                          </c:when>
                          <c:otherwise>
                            <a href="${pageContext.request.contextPath}/ServletUsuario?acao=reativar&id=${u.idUsuario}" title="Reativar"><i data-lucide="user-check"></i></a>
                          </c:otherwise>
                        </c:choose>
                      </span>
                    </td>
                  </tr>
                </c:forEach>
              </tbody>
            </table>
            <div class="empty" id="empty" style="${empty sessionScope.ListaUsuarios ? '' : 'display:none'}"><i data-lucide="users-round"></i><h4>Nenhum usuario encontrado</h4><p>Crie um novo usuario para liberar acesso.</p></div>
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
        <div><h2>${not empty sessionScope.User ? 'Editar usuario' : 'Novo usuario'}</h2><p>Defina credenciais, perfil e setor</p></div>
        <button class="icon-btn" data-close type="button" title="Fechar"><i data-lucide="x"></i></button>
      </div>
      <form action="${pageContext.request.contextPath}/ServletUsuario" method="POST">
        <div class="drawer__body">
          <input type="hidden" name="id" value="${sessionScope.User.idUsuario}" />
          <div class="field"><label for="txtNome">Nome <span class="req">*</span></label><input class="input" id="txtNome" name="txtNome" value="${sessionScope.User.nomeUsuario}" required /></div>
          <div class="field"><label for="txtEmail">E-mail <span class="req">*</span></label><input class="input" id="txtEmail" name="txtEmail" type="email" value="${sessionScope.User.emailUsuario}" required /></div>
          <div class="field"><label for="txtSenha">Senha <c:if test="${empty sessionScope.User}"><span class="req">*</span></c:if></label><input class="input" id="txtSenha" name="txtSenha" type="password" placeholder="******* (deixe vazio para manter)" ${empty sessionScope.User ? 'required' : ''} /></div>
          <div class="field-row">
            <div class="field">
              <label for="txtPerfil">Perfil</label>
              <div class="select-wrap">
                <select class="select" id="txtPerfil" name="txtPerfil">
                  <option value="admin" ${sessionScope.User.perfilUsuario eq 'admin' ? 'selected' : ''}>Administrador</option>
                  <option value="gerente" ${sessionScope.User.perfilUsuario eq 'gerente' ? 'selected' : ''}>Gerente</option>
                  <option value="usuario" ${empty sessionScope.User or sessionScope.User.perfilUsuario eq 'usuario' ? 'selected' : ''}>Usuario</option>
                </select>
              </div>
            </div>
            <div class="field">
              <label for="idSetor">Setor</label>
              <div class="select-wrap">
                <select class="select" id="idSetor" name="idSetor">
                  <option value="">Selecione...</option>
                  <c:forEach items="${sessionScope.ListaSetores}" var="s">
                    <option value="${s.idSetor}" ${sessionScope.User.idSetorUsuario == s.idSetor ? 'selected' : ''}>${s.nomeSetor}</option>
                  </c:forEach>
                </select>
              </div>
            </div>
          </div>
        </div>
        <div class="drawer__foot">
          <button class="btn btn--secondary" type="button" data-close>Cancelar</button>
          <button class="btn btn--primary" type="submit"><i data-lucide="save"></i> Salvar usuario</button>
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
      var rows = Array.prototype.slice.call(document.querySelectorAll("#tbodyUsuarios tr"));
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
  <c:remove var="User" scope="session"/>
</body>
</html>
