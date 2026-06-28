<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<header class="topbar">
  <button class="icon-btn" id="btnSidebar" title="Recolher menu" type="button">
    <i data-lucide="panel-left"></i>
  </button>
  <nav class="topbar__crumbs">
    <span>GDI</span>
    <i data-lucide="chevron-right"></i>
    <b>${empty requestScope.pageTitle ? 'Painel' : requestScope.pageTitle}</b>
  </nav>

  <form class="search" id="globalSearchForm" action="${pageContext.request.contextPath}/views/demandas.jsp" method="get">
    <i data-lucide="search"></i>
    <input id="globalSearch" name="q" type="search" value="${param.q}" placeholder="Buscar demandas, setores..." autocomplete="off" />
    <kbd>/</kbd>
  </form>

  <div class="topbar__actions">
    <div class="notif">
      <button class="icon-btn notif__btn" id="btnNotifications" title="Notificacoes" type="button" aria-expanded="false">
        <i data-lucide="bell"></i>
        <c:if test="${sessionScope.TotalNotificacoes gt 0}">
          <span class="notif__badge">${sessionScope.TotalNotificacoes}</span>
        </c:if>
      </button>
      <div class="notif__menu" id="notificationMenu" hidden="hidden">
        <div class="notif__head">
          <div class="notif__head-title">
            <strong>Notificacoes</strong>
            <span>${empty sessionScope.TotalNotificacoes ? 0 : sessionScope.TotalNotificacoes}</span>
          </div>
          <form action="${pageContext.request.contextPath}/ServletNotificacao" method="post">
            <button class="icon-btn notif__mark" title="Marcar como lidas" type="submit" <c:if test="${empty sessionScope.TotalNotificacoes or sessionScope.TotalNotificacoes == 0}">disabled</c:if>>
              <i data-lucide="check-check"></i>
            </button>
          </form>
        </div>

        <c:forEach items="${sessionScope.ListaDemandas}" var="d">
          <c:if test="${d.slaStatus eq 'vencido'}">
            <a class="notif__item" href="${pageContext.request.contextPath}/ServletAbrirNotificacao?chave=sla:${d.idDemanda}&idDemanda=${d.idDemanda}">
              <span class="notif__dot notif__dot--danger"></span>
              <span><b>SLA vencido</b><small>#${d.idDemanda} - ${d.tituloDemanda}</small></span>
            </a>
          </c:if>
        </c:forEach>

        <c:forEach items="${sessionScope.ListaNotificacoesComentarios}" var="n">
          <a class="notif__item" href="${pageContext.request.contextPath}/ServletAbrirNotificacao?chave=comentario:${n.idComentario}&idDemanda=${n.idDemandaComentario}&idComentario=${n.idComentario}">
            <span class="notif__dot notif__dot--info"></span>
            <span>
              <b>${empty n.nomeUsuario ? 'Usuario' : n.nomeUsuario} comentou</b>
              <small>#${n.idDemandaComentario} - ${empty n.tituloDemanda ? 'Demanda' : n.tituloDemanda}</small>
            </span>
          </a>
        </c:forEach>

        <c:forEach items="${sessionScope.ListaNotificacoesAnexos}" var="a">
          <a class="notif__item" href="${pageContext.request.contextPath}/ServletAbrirNotificacao?chave=anexo:${a.idAnexo}&idDemanda=${a.idDemandaAnexo}&idAnexo=${a.idAnexo}">
            <span class="notif__dot notif__dot--attach"></span>
            <span>
              <b>${empty a.nomeUsuarioUpload ? 'Usuario' : a.nomeUsuarioUpload} anexou arquivo</b>
              <small>#${a.idDemandaAnexo} - ${a.nomeArquivoAnexo}</small>
            </span>
          </a>
        </c:forEach>

        <c:if test="${empty sessionScope.TotalNotificacoesAtuais or sessionScope.TotalNotificacoesAtuais == 0}">
          <div class="notif__empty">Nada novo por enquanto.</div>
        </c:if>
      </div>
    </div>
  </div>
</header>
