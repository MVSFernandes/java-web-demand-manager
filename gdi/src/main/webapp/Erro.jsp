<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="pt-BR">
<%@ include file="views/templates/header.jsp" %>
<body>
  <main class="login-main" style="min-height:100vh;">
    <section class="card" style="max-width:520px;width:100%;">
      <div class="card__body" style="text-align:center;padding:34px;">
        <div class="brand-mark" style="margin:0 auto 16px;">!</div>
        <h1 style="margin:0 0 8px;font-family:var(--font-head);color:var(--slate-100);">Ops, algo deu errado</h1>
        <p style="margin:0 0 22px;color:var(--slate-400);">${empty requestScope.Msg ? 'Nao foi possivel concluir a operacao.' : requestScope.Msg}</p>
        <a href="${pageContext.request.contextPath}/views/principal.jsp" class="btn btn--primary">Voltar ao painel</a>
      </div>
    </section>
  </main>
  <script src="https://unpkg.com/lucide@0.456.0/dist/umd/lucide.min.js"></script>
  <script>if (window.lucide) lucide.createIcons();</script>
</body>
</html>
