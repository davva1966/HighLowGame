package au.com.highlowgame.service;

import java.sql.Connection;

import org.springframework.stereotype.Service;

import au.com.highlowgame.model.Game;
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;

@Service("reportService")
public interface ReportService {

	public JasperReportBuilder getGameReportBuilder(Game game, Connection con) throws Exception;

}
