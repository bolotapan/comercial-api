package com.trainer.comercial.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.lowagie.text.pdf.codec.Base64.InputStream;
import com.trainer.comercial.controller.OportunidadeController;
import com.trainer.comercial.model.Oportunidade;
import com.trainer.comercial.repository.OportunidadeRepository;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class ReportService {
	
	@Autowired
	private OportunidadeRepository repository;

	public byte[] exportReport(String reportName) throws JRException, FileNotFoundException {
		List<Oportunidade> oportunidades = repository.findAll();
		
		JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(oportunidades);
		
		File file = ResourceUtils.getFile("classpath:report/" + reportName + ".jasper");
		
		JasperPrint jasperPrint = JasperFillManager.fillReport(file.getAbsolutePath(), new HashMap<String, Object>(), dataSource);
		
		return JasperExportManager.exportReportToPdf(jasperPrint);
	}

}
