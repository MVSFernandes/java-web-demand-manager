<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="pt-BR">
<%@ include file="views/templates/header.jsp" %>
<body>
  <div class="login-shell">
    <aside class="login-aside">
      <div class="login-aside__brand">
        <div class="brand-mark">G</div>
        <div>
          <strong>GDI</strong>
          <span>Gerenciamento de Demandas Internas</span>
        </div>
      </div>

      <div class="login-aside__pitch">
        <h2>Cada demanda no lugar certo.</h2>
        <p>Centralize solicitacoes entre setores, acompanhe prazos de SLA e mantenha o historico completo de cada demanda.</p>
      </div>

      <div class="login-aside__foot">2026 GDI - Todos Direitos Reservados</div>
    </aside>

    <main class="login-main">
      <div class="login-card">
        <div class="login-card__head">
          <div class="brand-mark">G</div>
          <h1>Entrar</h1>
          <p>Acesse o painel com suas credenciais corporativas</p>
        </div>

        <c:if test="${not empty sessionScope.Msg or not empty requestScope.Msg}">
          <div class="alert">
            <i data-lucide="alert-octagon"></i>
            <p>${empty sessionScope.Msg ? requestScope.Msg : sessionScope.Msg}</p>
          </div>
          <c:remove var="Msg" scope="session"/>
        </c:if>

        <form class="login-form" action="${pageContext.request.contextPath}/ServletLogin" method="POST">
          <div class="field">
            <label for="txtEmail">E-mail corporativo</label>
            <div class="input-icon">
              <i data-lucide="mail"></i>
              <input class="input" type="email" id="txtEmail" name="txtEmail" placeholder="nome@empresa.com.br" autocomplete="username" required />
            </div>
          </div>

          <div class="field">
            <label for="txtSenha">Senha</label>
            <div class="input-icon">
              <i data-lucide="lock"></i>
              <input class="input" type="password" id="txtSenha" name="txtSenha" placeholder="********" autocomplete="current-password" required />
            </div>
          </div>

          <div class="login-extra">
            <label><input type="checkbox" name="lembrar" /> Manter conectado</label>
          </div>

          <button type="submit" class="btn btn--primary"><i data-lucide="log-in"></i> Entrar</button>
        </form>
      </div>
    </main>
  </div>

  <div class="toast-stack" id="toastStack"></div>
  <script src="https://unpkg.com/lucide@0.456.0/dist/umd/lucide.min.js"></script>
  <script>
    if (window.lucide) lucide.createIcons();
  </script>
</body>
</html>
