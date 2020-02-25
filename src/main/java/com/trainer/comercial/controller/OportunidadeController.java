package com.trainer.comercial.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.flywaydb.core.internal.util.FileCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.trainer.comercial.model.Oportunidade;
import com.trainer.comercial.repository.OportunidadeRepository;
import com.trainer.comercial.service.ReportService;

import net.sf.jasperreports.engine.JRException;

// GET http://localhost:8080/oportunidades

// permite que o navegador acesse origens cruzadas (acessos de outros locais)
//@CrossOrigin("http://localhost:4200") //informa a url especifica que pode acessar 
@CrossOrigin // permite qualquer um
@RestController // determina que a classe é um controlador rest
@RequestMapping("/oportunidades") // uri de mapeamento
public class OportunidadeController {

	@Autowired // instancia uma interface
	private OportunidadeRepository oportunidades;

	@Autowired
	private ReportService reportService;

	@GetMapping
	public List<Oportunidade> listar() {
		return oportunidades.findAll();
	}

	// Optional = representa que essa pesquisa/busca pode não ter nada
	@GetMapping("/{id}")
	public ResponseEntity<Oportunidade> buscar(@PathVariable Long id) {
		Optional<Oportunidade> oportunidade = oportunidades.findById(id);
		if (oportunidade.isPresent()) {
			return ResponseEntity.ok(oportunidade.get());
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED) // ele diz que o metood tem que retornar o status CREATED
	public Oportunidade adicionar(@Valid @RequestBody Oportunidade oportunidade) {
		Optional<Oportunidade> oportunidadeExistente = oportunidades
				.findByDescricaoAndNomeProspecto(oportunidade.getDescricao(), oportunidade.getNomeProspecto());

		if (oportunidadeExistente.isPresent()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Já existe uma oportunidade para esse prospecto com a mesma descrição.");
		}
		return oportunidades.save(oportunidade);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Oportunidade> alterar(@PathVariable(value = "id") Long id,
			@Valid @RequestBody Oportunidade oportunidade) {
		Optional<Oportunidade> oportunidadeExists = oportunidades.findById(id);
		if (oportunidadeExists.isPresent()) {
			oportunidade.setId(id);
			oportunidade = oportunidades.save(oportunidade);
			return ResponseEntity.ok(oportunidade);
		} else {
			return ResponseEntity.notFound().build();
		}

	}

	@DeleteMapping("/{id}")
	public void excluir(@PathVariable Long id) {
		oportunidades.deleteById(id);
	}

	@GetMapping("/report/lista")
	public void exportReport(HttpServletResponse response) throws JRException, IOException {
		try {
			byte[] pdfReport = this.reportService.exportReport("oportunidades");
			
			String fileName = "Oportunidades";

			response.setContentType("application/force-download");
			response.setHeader("Content-Disposition", String.format("attachment; filename=%s.pdf", fileName));
			
			InputStream is = new ByteArrayInputStream(pdfReport);
			
			FileCopyUtils.copy(is, response.getOutputStream());
			
			response.getOutputStream().flush();
		} catch (JRException e) {
			response.setStatus(HttpServletResponse.SC_OK);
		}
	}

}
