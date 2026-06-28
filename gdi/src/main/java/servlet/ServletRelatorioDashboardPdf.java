package servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.DAODemanda;
import models.Demandas;
import models.Usuarios;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

@WebServlet("/ServletRelatorioDashboardPdf")
public class ServletRelatorioDashboardPdf extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Usuarios usuarioLogado = (Usuarios) request.getSession().getAttribute("UsuarioLogado");
        if (usuarioLogado == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        try {
            int periodoDias = lerPeriodoDias(request);
            String periodoLabel = periodoDias == 0 ? "Todo o periodo" : "Ultimos " + periodoDias + " dias";
            boolean podeVerTudo = "admin".equals(usuarioLogado.getPerfilUsuario())
                || "gerente".equals(usuarioLogado.getPerfilUsuario());

            DAODemanda daoDemanda = new DAODemanda();
            List<Demandas> demandas = podeVerTudo
                ? daoDemanda.listarTodasDemandas()
                : daoDemanda.listarDemandasPorSolicitante(usuarioLogado.getIdUsuario());
            demandas = filtrarPorPeriodo(demandas, periodoDias);

            int abertas = 0;
            int concluidas = 0;
            int vencidas = 0;
            for (Demandas demanda : demandas) {
                String status = demanda.getNomeStatus();
                if ((demanda.getIdStatusDemanda() != null && demanda.getIdStatusDemanda() == 1)
                        || "Aberta".equalsIgnoreCase(status)) {
                    abertas++;
                }
                if ((demanda.getIdStatusDemanda() != null && demanda.getIdStatusDemanda() == 4)
                        || (status != null && status.toLowerCase().contains("conclu"))) {
                    concluidas++;
                }
                if ("vencido".equals(demanda.getSlaStatus())) {
                    vencidas++;
                }
            }

            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            List<Map<String, ?>> linhas = new ArrayList<>();
            for (Demandas demanda : demandas) {
                Map<String, Object> linha = new HashMap<>();
                linha.put("id", "#" + demanda.getIdDemanda());
                linha.put("titulo", texto(demanda.getTituloDemanda()));
                linha.put("prioridade", texto(demanda.getPrioridadeTexto()));
                linha.put("status", texto(demanda.getNomeStatus()));
                linha.put("setor", texto(demanda.getNomeSetorDestino()));
                linha.put("categoria", texto(demanda.getNomeCategoria()));
                linha.put("solicitante", texto(demanda.getNomeSolicitante()));
                linha.put("sla", texto(demanda.getSlaTexto()));
                linha.put("abertura", demanda.getAbertaEmDemanda() == null ? "" : formato.format(demanda.getAbertaEmDemanda()));
                linhas.add(linha);
            }

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("periodo", periodoLabel);
            parametros.put("usuario", usuarioLogado.getNomeUsuario());
            parametros.put("geradoEm", formato.format(new java.util.Date()));
            parametros.put("total", String.valueOf(demandas.size()));
            parametros.put("abertas", String.valueOf(abertas));
            parametros.put("concluidas", String.valueOf(concluidas));
            parametros.put("vencidas", String.valueOf(vencidas));

            // The JRXML is inline so deploying the WAR does not depend on a separate report file.
            JasperReport report = JasperCompileManager.compileReport(
                new ByteArrayInputStream(jrxml().getBytes(StandardCharsets.UTF_8)));
            JasperPrint print = JasperFillManager.fillReport(report, parametros, new JRMapCollectionDataSource(linhas));

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"gdi-dashboard.pdf\"");
            JasperExportManager.exportReportToPdfStream(print, response.getOutputStream());
        } catch (Exception e) {
            throw new ServletException("Erro ao gerar relatorio PDF: " + e.getMessage(), e);
        }
    }

    private int lerPeriodoDias(HttpServletRequest request) {
        String periodo = request.getParameter("periodo");
        if ("7".equals(periodo)) return 7;
        if ("90".equals(periodo)) return 90;
        if ("todos".equals(periodo)) return 0;
        return 30;
    }

    private List<Demandas> filtrarPorPeriodo(List<Demandas> demandas, int periodoDias) {
        if (periodoDias <= 0) {
            return demandas;
        }

        LocalDateTime limite = LocalDateTime.now().minusDays(periodoDias);
        List<Demandas> filtradas = new ArrayList<>();
        for (Demandas demanda : demandas) {
            if (demanda.getAbertaEmDemanda() != null
                    && !demanda.getAbertaEmDemanda().toLocalDateTime().isBefore(limite)) {
                filtradas.add(demanda);
            }
        }
        return filtradas;
    }

    private String texto(Object valor) {
        return valor == null ? "" : String.valueOf(valor);
    }

    private String jrxml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<jasperReport xmlns=\"http://jasperreports.sourceforge.net/jasperreports\" " +
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
            "xsi:schemaLocation=\"http://jasperreports.sourceforge.net/jasperreports http://jasperreports.sourceforge.net/xsd/jasperreport.xsd\" " +
            "name=\"gdi_dashboard\" pageWidth=\"842\" pageHeight=\"595\" orientation=\"Landscape\" columnWidth=\"802\" leftMargin=\"20\" rightMargin=\"20\" topMargin=\"20\" bottomMargin=\"20\">" +
            "<style name=\"h1\" fontName=\"SansSerif\" fontSize=\"20\" isBold=\"true\" forecolor=\"#0F172A\"/>" +
            "<style name=\"meta\" fontName=\"SansSerif\" fontSize=\"9\" forecolor=\"#64748B\"/>" +
            "<style name=\"kpi\" fontName=\"SansSerif\" fontSize=\"16\" isBold=\"true\" forecolor=\"#0F172A\"/>" +
            "<style name=\"label\" fontName=\"SansSerif\" fontSize=\"8\" isBold=\"true\" forecolor=\"#64748B\"/>" +
            "<style name=\"head\" fontName=\"SansSerif\" fontSize=\"8\" isBold=\"true\" forecolor=\"#FFFFFF\" mode=\"Opaque\" backcolor=\"#E07B39\"/>" +
            "<style name=\"cell\" fontName=\"SansSerif\" fontSize=\"8\" forecolor=\"#1E293B\"/>" +
            "<parameter name=\"periodo\" class=\"java.lang.String\"/>" +
            "<parameter name=\"usuario\" class=\"java.lang.String\"/>" +
            "<parameter name=\"geradoEm\" class=\"java.lang.String\"/>" +
            "<parameter name=\"total\" class=\"java.lang.String\"/>" +
            "<parameter name=\"abertas\" class=\"java.lang.String\"/>" +
            "<parameter name=\"concluidas\" class=\"java.lang.String\"/>" +
            "<parameter name=\"vencidas\" class=\"java.lang.String\"/>" +
            "<field name=\"id\" class=\"java.lang.String\"/>" +
            "<field name=\"titulo\" class=\"java.lang.String\"/>" +
            "<field name=\"prioridade\" class=\"java.lang.String\"/>" +
            "<field name=\"status\" class=\"java.lang.String\"/>" +
            "<field name=\"setor\" class=\"java.lang.String\"/>" +
            "<field name=\"categoria\" class=\"java.lang.String\"/>" +
            "<field name=\"solicitante\" class=\"java.lang.String\"/>" +
            "<field name=\"sla\" class=\"java.lang.String\"/>" +
            "<field name=\"abertura\" class=\"java.lang.String\"/>" +
            "<title><band height=\"126\">" +
            "<textField><reportElement style=\"h1\" x=\"0\" y=\"0\" width=\"360\" height=\"28\"/><textFieldExpression><![CDATA[\"Relatorio do Painel GDI\"]]></textFieldExpression></textField>" +
            "<textField><reportElement style=\"meta\" x=\"0\" y=\"28\" width=\"600\" height=\"16\"/><textFieldExpression><![CDATA[\"Periodo: \" + $P{periodo} + \" | Gerado em: \" + $P{geradoEm} + \" | Usuario: \" + $P{usuario}]]></textFieldExpression></textField>" +
            "<rectangle><reportElement x=\"0\" y=\"58\" width=\"190\" height=\"48\" forecolor=\"#CBD5E1\" backcolor=\"#F8FAFC\" mode=\"Opaque\"/></rectangle>" +
            "<rectangle><reportElement x=\"204\" y=\"58\" width=\"190\" height=\"48\" forecolor=\"#CBD5E1\" backcolor=\"#F8FAFC\" mode=\"Opaque\"/></rectangle>" +
            "<rectangle><reportElement x=\"408\" y=\"58\" width=\"190\" height=\"48\" forecolor=\"#CBD5E1\" backcolor=\"#F8FAFC\" mode=\"Opaque\"/></rectangle>" +
            "<rectangle><reportElement x=\"612\" y=\"58\" width=\"190\" height=\"48\" forecolor=\"#CBD5E1\" backcolor=\"#F8FAFC\" mode=\"Opaque\"/></rectangle>" +
            "<textField><reportElement style=\"label\" x=\"12\" y=\"66\" width=\"150\" height=\"12\"/><textFieldExpression><![CDATA[\"TOTAL DE DEMANDAS\"]]></textFieldExpression></textField>" +
            "<textField><reportElement style=\"kpi\" x=\"12\" y=\"80\" width=\"150\" height=\"22\"/><textFieldExpression><![CDATA[$P{total}]]></textFieldExpression></textField>" +
            "<textField><reportElement style=\"label\" x=\"216\" y=\"66\" width=\"150\" height=\"12\"/><textFieldExpression><![CDATA[\"ABERTAS\"]]></textFieldExpression></textField>" +
            "<textField><reportElement style=\"kpi\" x=\"216\" y=\"80\" width=\"150\" height=\"22\"/><textFieldExpression><![CDATA[$P{abertas}]]></textFieldExpression></textField>" +
            "<textField><reportElement style=\"label\" x=\"420\" y=\"66\" width=\"150\" height=\"12\"/><textFieldExpression><![CDATA[\"CONCLUIDAS\"]]></textFieldExpression></textField>" +
            "<textField><reportElement style=\"kpi\" x=\"420\" y=\"80\" width=\"150\" height=\"22\"/><textFieldExpression><![CDATA[$P{concluidas}]]></textFieldExpression></textField>" +
            "<textField><reportElement style=\"label\" x=\"624\" y=\"66\" width=\"150\" height=\"12\"/><textFieldExpression><![CDATA[\"SLA VENCIDO\"]]></textFieldExpression></textField>" +
            "<textField><reportElement style=\"kpi\" x=\"624\" y=\"80\" width=\"150\" height=\"22\"/><textFieldExpression><![CDATA[$P{vencidas}]]></textFieldExpression></textField>" +
            "</band></title>" +
            "<columnHeader><band height=\"22\">" +
            campoCabecalho("ID", 0, 44) + campoCabecalho("Titulo", 44, 188) + campoCabecalho("Prioridade", 232, 70) + campoCabecalho("Status", 302, 86) + campoCabecalho("Setor", 388, 90) + campoCabecalho("Categoria", 478, 100) + campoCabecalho("Solicitante", 578, 100) + campoCabecalho("SLA", 678, 48) + campoCabecalho("Abertura", 726, 76) +
            "</band></columnHeader>" +
            "<detail><band height=\"24\">" +
            campoDetalhe("id", 0, 44) + campoDetalhe("titulo", 44, 188) + campoDetalhe("prioridade", 232, 70) + campoDetalhe("status", 302, 86) + campoDetalhe("setor", 388, 90) + campoDetalhe("categoria", 478, 100) + campoDetalhe("solicitante", 578, 100) + campoDetalhe("sla", 678, 48) + campoDetalhe("abertura", 726, 76) +
            "</band></detail>" +
            "<pageFooter><band height=\"18\"><textField><reportElement style=\"meta\" x=\"0\" y=\"2\" width=\"802\" height=\"14\"/><textFieldExpression><![CDATA[\"Pagina \" + $V{PAGE_NUMBER}]]></textFieldExpression></textField></band></pageFooter>" +
            "</jasperReport>";
    }

    private String campoCabecalho(String label, int x, int w) {
        return "<staticText><reportElement style=\"head\" x=\"" + x + "\" y=\"0\" width=\"" + w + "\" height=\"20\"/>" +
            "<box leftPadding=\"4\" rightPadding=\"4\"><pen lineWidth=\"0.5\" lineColor=\"#FFFFFF\"/></box>" +
            "<text><![CDATA[" + label + "]]></text></staticText>";
    }

    private String campoDetalhe(String field, int x, int w) {
        return "<textField textAdjust=\"StretchHeight\"><reportElement style=\"cell\" x=\"" + x + "\" y=\"0\" width=\"" + w + "\" height=\"22\"/>" +
            "<box leftPadding=\"4\" rightPadding=\"4\"><pen lineWidth=\"0.5\" lineColor=\"#E2E8F0\"/></box>" +
            "<textFieldExpression><![CDATA[$F{" + field + "}]]></textFieldExpression></textField>";
    }
}
