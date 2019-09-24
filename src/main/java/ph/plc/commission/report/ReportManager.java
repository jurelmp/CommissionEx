package ph.plc.commission.report;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

import javax.inject.Singleton;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

@Singleton
public class ReportManager {

    public enum REPORT {
        DRIVER_COMMISSION_SUMMARY_REPORT("/reports/DriverCommissionSummary.jrxml"),
        DRIVERS_COMMISSION_SUMMARY_REPORT("/reports/DriversCommissionSummary.jrxml"),
        HAULING_INCOME_REPORT("/reports/HaulingIncome.jrxml"),
        DELIVERY_CHARGES_REPORT("/reports/DeliveryCharges.jrxml");

        private String reportName;

        REPORT(String templatePath) {
            reportName = templatePath;
        }

        public String getReportTemplate() {
            return reportName;
        }
    }

    public enum KEY {
        PERIOD_FROM("PERIOD_FROM"),
        PERIOD_TO("PERIOD_TO");

        private String key;

        KEY(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }

    public void generateReport(REPORT report, Map parameters) throws JRException, ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver");
        Connection connection =
                DriverManager.getConnection("jdbc:h2:file:./data/commission;AUTO_SERVER=TRUE");

        JasperReport jasperReport = JasperCompileManager.compileReport(
                getClass().getResourceAsStream(report.getReportTemplate()));
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);
        JasperViewer.viewReport(jasperPrint, false);
    }
}
