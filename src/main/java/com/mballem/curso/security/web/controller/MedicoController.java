package com.mballem.curso.security.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mballem.curso.security.domain.Medico;
import com.mballem.curso.security.domain.Usuario;
import com.mballem.curso.security.service.MedicoService;
import com.mballem.curso.security.service.UsuarioService;

@Controller
@RequestMapping("medicos")
public class MedicoController {
	
	@Autowired
	private MedicoService medicoService;
	
	@Autowired
	private UsuarioService usuarioService;

	//abrir pagina de dados pessoais de medicos pelo MEDICO
	@GetMapping({"/dados"})
	public String abrirPorMedico(Medico medico, ModelMap model,  @AuthenticationPrincipal User user) {
		if(medico.hasNotId()) {
			medico = medicoService.buscarPorEmailUsuario(user.getUsername());
			model.addAttribute("medico", medico);
		}
		return "medico/cadastro";
	}
	
	//salvar medico
	@PostMapping({"/salvar"})
	public String salvar(Medico medico, RedirectAttributes attr, @AuthenticationPrincipal User user
			/*, Principal principal, Authentication authentication*/) {
		
		//user.getUsername();
		//principal.getName();
		//authentication.getName();
		
		if(medico.hasNotId() && medico.getUsuario().hasNotId()) {
			Usuario usuario = usuarioService.buscarPorEmail(user.getUsername());
			medico.setUsuario(usuario);
		}
		
		medicoService.salvar(medico);
		attr.addAttribute("sucesso","Operação realizada com sucesso.");
		attr.addAttribute("medico", medico);
		return "redirect:/medicos/dados";
	}
	
	//editar medico
	@PostMapping({"/editar"})
	public String editar(Medico medico, RedirectAttributes attr) {
		medicoService.editar(medico);
		attr.addAttribute("sucesso","Operação realizada com sucesso.");
		attr.addAttribute("medico", medico);
		return "redirect:/medicos/dados";
	}
	
	@GetMapping("/id/{idMedico}/excluir/especializacao/{idEspecializacao}")
	public String excluirEspecializacaoPorMedico(@PathVariable("idMedico") Long idMedico, 
												 @PathVariable("idEspecializacao") Long idEspecializacao,
												 RedirectAttributes attr){
		if(medicoService.existeEspecialidadeAgendada(idMedico, idEspecializacao)) {
			attr.addFlashAttribute("falha", "Tem consultas agendadas, exclusão negada.");
		}else {
			medicoService.excluirEspecializacaoPorMedico(idMedico, idEspecializacao);
			attr.addFlashAttribute("sucesso", "Especialidade removida com sucesso.");
		}
		
		return "redirect:/medicos/dados";
	}
	
	@GetMapping("/especialidade/titulo/{titulo}")
	public ResponseEntity<?> getMedicosPorEspecialidade(@PathVariable("titulo") String titulo){
		return ResponseEntity.ok(medicoService.buscarMedicoPorEspecialidade(titulo));
	}
	
}
