<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<c:set var="pageTitle" value="Painel" scope="request" />
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
            <h1>Painel operacional</h1>
            <p>Visao geral das demandas internas &middot; atualizado ${empty requestScope.dashboardAtualizadoTexto ? 'agora' : requestScope.dashboardAtualizadoTexto}</p>
          </div>
          <div class="pagehead__actions">
            <form class="range-filter" action="${pageContext.request.contextPath}/views/principal.jsp" method="get">
              <i data-lucide="calendar-days"></i>
              <select name="periodo" onchange="this.form.submit()" title="Periodo do painel">
                <option value="7" ${requestScope.periodoDias == 7 ? 'selected' : ''}>Ultimos 7 dias</option>
                <option value="30" ${empty requestScope.periodoDias or requestScope.periodoDias == 30 ? 'selected' : ''}>Ultimos 30 dias</option>
                <option value="90" ${requestScope.periodoDias == 90 ? 'selected' : ''}>Ultimos 90 dias</option>
                <option value="todos" ${requestScope.periodoDias == 0 ? 'selected' : ''}>Todo o periodo</option>
              </select>
            </form>
            <a class="btn btn--secondary btn--sm btn--icon" href="${pageContext.request.contextPath}/ServletExportarDashboard?periodo=${requestScope.periodoDias == 0 ? 'todos' : requestScope.periodoDias}" title="Exportar painel em Excel">
              <i data-lucide="download"></i>
            </a>
            <a class="btn btn--secondary btn--sm btn--icon" href="${pageContext.request.contextPath}/ServletRelatorioDashboardPdf?periodo=${requestScope.periodoDias == 0 ? 'todos' : requestScope.periodoDias}" title="Gerar relatorio PDF com JasperReports">
              <i data-lucide="file-text"></i>
            </a>
          </div>
        </div>

        <section class="kpi-grid">
          <article class="kpi">
            <div class="kpi__top">
              <span class="kpi__label">Total de demandas</span>
              <span class="kpi__icon kpi__icon--blue"><i data-lucide="layers"></i></span>
            </div>
            <div class="kpi__num">${empty requestScope.totalDemandas ? 0 : requestScope.totalDemandas}</div>
            <div class="kpi__foot">
              <span class="trend trend--up"><i data-lucide="trending-up"></i>${empty requestScope.totalDemandas ? 0 : 100}%</span>
              <span class="muted">registradas no sistema</span>
            </div>
          </article>

          <article class="kpi">
            <div class="kpi__top">
              <span class="kpi__label">Abertas</span>
              <span class="kpi__icon kpi__icon--accent"><i data-lucide="folder-open"></i></span>
            </div>
            <div class="kpi__num">${empty requestScope.totalAbertas ? 0 : requestScope.totalAbertas}</div>
            <div class="kpi__foot">
              <span class="trend trend--up"><i data-lucide="trending-up"></i>${empty requestScope.percentAbertas ? 0 : requestScope.percentAbertas}%</span>
              <span class="muted">em andamento</span>
            </div>
          </article>

          <article class="kpi">
            <div class="kpi__top">
              <span class="kpi__label">Concluidas</span>
              <span class="kpi__icon kpi__icon--green"><i data-lucide="circle-check-big"></i></span>
            </div>
            <div class="kpi__num">${empty requestScope.totalConcluidas ? 0 : requestScope.totalConcluidas}</div>
            <div class="kpi__foot">
              <span class="trend trend--up"><i data-lucide="trending-up"></i>${empty requestScope.percentConcluidas ? 0 : requestScope.percentConcluidas}%</span>
              <span class="muted">no periodo</span>
            </div>
          </article>

          <article class="kpi">
            <div class="kpi__top">
              <span class="kpi__label">SLA vencido</span>
              <span class="kpi__icon kpi__icon--red"><i data-lucide="alarm-clock-off"></i></span>
            </div>
            <div class="kpi__num">${empty requestScope.totalVencidas ? 0 : requestScope.totalVencidas}</div>
            <div class="kpi__foot">
              <span class="trend trend--down"><i data-lucide="trending-down"></i>${empty requestScope.percentVencidas ? 0 : requestScope.percentVencidas}%</span>
              <span class="muted">requer atencao</span>
            </div>
          </article>
        </section>

        <div class="dash-grid">
          <section class="card">
            <div class="card__head">
              <h3>Demandas recentes</h3>
              <a class="btn btn--ghost btn--sm" href="${pageContext.request.contextPath}/views/demandas.jsp">Ver todas <i data-lucide="arrow-right"></i></a>
            </div>
            <div class="table-wrap">
              <table class="tbl">
                <thead>
                  <tr>
                    <th style="width:54px">#</th>
                    <th>Demanda</th>
                    <th>Prioridade</th>
                    <th>Setor</th>
                    <th>Status</th>
                    <th>SLA</th>
                    <th style="width:96px"></th>
                  </tr>
                </thead>
                <tbody>
                  <c:forEach items="${requestScope.ListaDemandasPainel}" var="demanda" end="7">
                    <tr>
                      <td class="cell-id">#${demanda.idDemanda}</td>
                      <td class="cell-title">${demanda.tituloDemanda}</td>
                      <td><span class="badge badge--${demanda.prioridadeCss}">${demanda.prioridadeTexto}</span></td>
                      <td>${empty demanda.nomeSetorDestino ? '-' : demanda.nomeSetorDestino}</td>
                      <td><span class="pill"><span class="dot" style="background:var(--blue)"></span>${empty demanda.nomeStatus ? '-' : demanda.nomeStatus}</span></td>
                      <td>
                        <c:choose>
                          <c:when test="${demanda.slaStatus eq 'sem-sla'}"><span class="muted">-</span></c:when>
                          <c:otherwise><span class="sla sla--${demanda.slaStatus}"><i data-lucide="${demanda.slaIcone}"></i>${demanda.slaTexto}</span></c:otherwise>
                        </c:choose>
                      </td>
                      <td class="cell-actions">
                        <span class="row-actions">
                          <a href="${pageContext.request.contextPath}/ServletDemanda?acao=comentarios&id=${demanda.idDemanda}" title="Comentarios"><i data-lucide="message-square"></i></a>
                          <a href="${pageContext.request.contextPath}/ServletDemanda?acao=carregar&id=${demanda.idDemanda}" title="Editar"><i data-lucide="pencil"></i></a>
                          <a class="is-danger js-confirm" href="${pageContext.request.contextPath}/ServletDemanda?acao=excluir&id=${demanda.idDemanda}" data-msg="Excluir a demanda #${demanda.idDemanda}?" title="Excluir"><i data-lucide="trash-2"></i></a>
                        </span>
                      </td>
                    </tr>
                  </c:forEach>
                  <c:if test="${empty requestScope.ListaDemandasPainel}">
                    <tr><td colspan="7"><div class="empty"><i data-lucide="inbox"></i><h4>Nenhuma demanda ainda</h4><p>Crie a primeira demanda para iniciar o fluxo.</p></div></td></tr>
                  </c:if>
                </tbody>
              </table>
            </div>
          </section>

          <div style="display:flex; flex-direction:column; gap:16px;">
            <section class="card">
              <div class="card__head"><h3>Por prioridade</h3></div>
              <div class="card__body" style="padding:8px 18px 14px;">
                <div class="bar-row">
                  <span class="bar-row__label"><span class="badge badge--alta">Alta</span></span>
                  <span class="bar-row__track"><span class="bar-row__fill bar-row__fill--alta" style="width:${requestScope.percentAlta}%"></span></span>
                  <span class="bar-row__val">${requestScope.totalAlta}</span>
                </div>
                <div class="bar-row">
                  <span class="bar-row__label"><span class="badge badge--media">Media</span></span>
                  <span class="bar-row__track"><span class="bar-row__fill bar-row__fill--media" style="width:${requestScope.percentMedia}%"></span></span>
                  <span class="bar-row__val">${requestScope.totalMedia}</span>
                </div>
                <div class="bar-row">
                  <span class="bar-row__label"><span class="badge badge--baixa">Baixa</span></span>
                  <span class="bar-row__track"><span class="bar-row__fill bar-row__fill--baixa" style="width:${requestScope.percentBaixa}%"></span></span>
                  <span class="bar-row__val">${requestScope.totalBaixa}</span>
                </div>
              </div>
            </section>

            <section class="card">
              <div class="card__head"><h3>Atividade</h3></div>
              <div class="card__body">
                <c:forEach items="${requestScope.ListaAtividades}" var="atividade">
                  <c:url var="atividadeUrl" value="/ServletDemanda">
                    <c:param name="acao" value="comentarios" />
                    <c:param name="id" value="${atividade.idDemanda}" />
                    <c:if test="${atividade.urlTipo eq 'anexo'}">
                      <c:param name="anexo" value="${atividade.urlId}" />
                    </c:if>
                    <c:if test="${atividade.urlTipo eq 'comentario'}">
                      <c:param name="comentario" value="${atividade.urlId}" />
                    </c:if>
                  </c:url>
                  <a class="feed-item feed-link" href="${atividadeUrl}">
                    <span class="feed-item__dot" style="background:${atividade.cor}"></span>
                    <div class="feed-item__body">
                      <p>
                        <c:choose>
                          <c:when test="${atividade.tipo eq 'sla'}">
                            <b>SLA</b> venceu na demanda <span class="feed-ref">#${atividade.idDemanda}</span>
                          </c:when>
                          <c:otherwise>
                            <b><c:out value="${atividade.autor}" /></b>
                            <c:out value=" ${atividade.acao} " />
                            <span class="feed-ref">#${atividade.idDemanda}</span>
                          </c:otherwise>
                        </c:choose>
                      </p>
                      <span class="feed-item__time"><c:out value="${atividade.tempoRelativo}" /> - <c:out value="${atividade.detalhe}" /></span>
                    </div>
                    <i class="feed-link__icon" data-lucide="${atividade.icone}"></i>
                  </a>
                </c:forEach>
                <c:if test="${empty requestScope.ListaAtividades}">
                  <div class="feed-item">
                    <span class="feed-item__dot" style="background:var(--green)"></span>
                    <div class="feed-item__body">
                      <p><b>Sistema</b> pronto para acompanhar novas atualizacoes.</p>
                      <span class="feed-item__time">agora</span>
                    </div>
                  </div>
                </c:if>
              </div>
            </section>
          </div>
        </div>
      </main>
    </div>

    <div class="scrim" id="scrim"></div>
  </div>
  <div class="toast-stack" id="toastStack"></div>
  <%@ include file="templates/javascript.jsp" %>
  <script>
    document.querySelectorAll(".js-confirm").forEach(function (link) {
      link.addEventListener("click", function (event) {
        if (!confirm(link.dataset.msg || "Confirmar acao?")) event.preventDefault();
      });
    });
  </script>
</body>
</html>
