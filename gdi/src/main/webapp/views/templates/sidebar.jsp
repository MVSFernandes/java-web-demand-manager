<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page import="models.Usuarios" %>
<%
  Usuarios usuarioSidebar = (Usuarios) session.getAttribute("UsuarioLogado");
  String perfilSidebar = usuarioSidebar != null ? usuarioSidebar.getPerfilUsuario() : "usuario";
  boolean isAdmin = "admin".equals(perfilSidebar);
  boolean isGerente = "gerente".equals(perfilSidebar);
  boolean isUsuario = "usuario".equals(perfilSidebar);
  String servletPath = request.getServletPath();
  String nomeSidebar = usuarioSidebar != null && usuarioSidebar.getNomeUsuario() != null ? usuarioSidebar.getNomeUsuario() : "Usuario";
  String iniciaisSidebar = nomeSidebar.length() >= 2 ? nomeSidebar.substring(0, 2).toUpperCase() : nomeSidebar.substring(0, 1).toUpperCase();
  String perfilTexto = isAdmin ? "Administrador" : (isGerente ? "Gerente" : "Usuario");
%>
<aside class="sidebar">
  <div class="sidebar__brand">
    <div class="brand-mark">G</div>
    <div class="brand-text">
      <strong>GDI</strong>
      <span>Demandas Internas</span>
    </div>
  </div>

  <nav class="sidebar__nav">
    <div class="nav-section">
      <div class="nav-section__label">Operacao</div>
      <a class="nav-item <%= servletPath.contains("principal.jsp") ? "is-active" : "" %>" href="${pageContext.request.contextPath}/views/principal.jsp" title="Painel">
        <i data-lucide="layout-dashboard"></i>
        <span class="nav-item__label">Painel</span>
      </a>
      <a class="nav-item <%= (servletPath.contains("demandas.jsp") || servletPath.contains("comentarios.jsp")) ? "is-active" : "" %>" href="${pageContext.request.contextPath}/views/demandas.jsp" title="Demandas">
        <i data-lucide="clipboard-list"></i>
        <span class="nav-item__label">Demandas</span>
        <span class="nav-item__count">${fn:length(sessionScope.ListaDemandas)}</span>
      </a>
    </div>

    <% if (isAdmin || isGerente) { %>
    <div class="nav-section">
      <div class="nav-section__label">Cadastros</div>
      <a class="nav-item <%= servletPath.contains("setores.jsp") ? "is-active" : "" %>" href="${pageContext.request.contextPath}/views/setores.jsp" title="Setores">
        <i data-lucide="network"></i>
        <span class="nav-item__label">Setores</span>
        <span class="nav-item__count">${fn:length(sessionScope.ListaSetores)}</span>
      </a>
      <a class="nav-item <%= servletPath.contains("categorias.jsp") ? "is-active" : "" %>" href="${pageContext.request.contextPath}/views/categorias.jsp" title="Categorias">
        <i data-lucide="tags"></i>
        <span class="nav-item__label">Categorias</span>
        <span class="nav-item__count">${fn:length(sessionScope.ListaCategorias)}</span>
      </a>
      <a class="nav-item <%= servletPath.contains("status.jsp") ? "is-active" : "" %>" href="${pageContext.request.contextPath}/views/status.jsp" title="Status">
        <i data-lucide="git-pull-request-arrow"></i>
        <span class="nav-item__label">Status</span>
        <span class="nav-item__count">${fn:length(sessionScope.ListaStatus)}</span>
      </a>
    </div>
    <% } %>

    <% if (isAdmin) { %>
    <div class="nav-section">
      <div class="nav-section__label">Administracao</div>
      <a class="nav-item <%= servletPath.contains("usuarios.jsp") ? "is-active" : "" %>" href="${pageContext.request.contextPath}/views/usuarios.jsp" title="Usuarios">
        <i data-lucide="users-round"></i>
        <span class="nav-item__label">Usuarios</span>
        <span class="nav-item__count">${fn:length(sessionScope.ListaUsuarios)}</span>
      </a>
    </div>
    <% } %>
  </nav>

  <div class="sidebar__foot">
    <div class="user-chip">
      <div class="avatar"><%= iniciaisSidebar %></div>
      <div class="user-chip__info">
        <strong>${sessionScope.UsuarioLogado.nomeUsuario}</strong>
        <span><%= perfilTexto %></span>
      </div>
      <a class="user-chip__logout" href="${pageContext.request.contextPath}/ServletLogin" title="Sair">
        <i data-lucide="log-out"></i>
      </a>
    </div>
  </div>
</aside>
