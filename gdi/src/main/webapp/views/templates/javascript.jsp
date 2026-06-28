<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:if test="${not empty sessionScope.Msg}">
  <script type="application/json" id="msg-session-data" data-tipo="${fn:containsIgnoreCase(sessionScope.Msg, 'ERRO') ? 'erro' : 'ok'}"><c:out value="${sessionScope.Msg}"/></script>
  <c:remove var="Msg" scope="session"/>
</c:if>

<script src="https://unpkg.com/lucide@0.456.0/dist/umd/lucide.min.js"></script>
<script src="${pageContext.request.contextPath}/scripts/shell.js"></script>
<script>
  (function () {
    var msgEl = document.getElementById("msg-session-data");
    if (msgEl && window.GDI && window.GDI.toast) {
      window.GDI.toast(msgEl.textContent, msgEl.dataset.tipo || "info");
    }

    var notifBtn = document.getElementById("btnNotifications");
    var notifMenu = document.getElementById("notificationMenu");
    if (notifBtn && notifMenu) {
      notifBtn.addEventListener("click", function (event) {
        event.stopPropagation();
        var open = notifMenu.hasAttribute("hidden");
        if (open) notifMenu.removeAttribute("hidden");
        else notifMenu.setAttribute("hidden", "hidden");
        notifBtn.setAttribute("aria-expanded", open ? "true" : "false");
      });
      document.addEventListener("click", function (event) {
        if (!notifMenu.contains(event.target) && event.target !== notifBtn) {
          notifMenu.setAttribute("hidden", "hidden");
          notifBtn.setAttribute("aria-expanded", "false");
        }
      });
    }

    var globalSearch = document.getElementById("globalSearch");
    if (globalSearch) {
      document.addEventListener("keydown", function (event) {
        if (event.key === "/" && document.activeElement !== globalSearch &&
            document.activeElement.tagName !== "INPUT" && document.activeElement.tagName !== "TEXTAREA") {
          event.preventDefault();
          globalSearch.focus();
        }
      });
    }
  })();
</script>
