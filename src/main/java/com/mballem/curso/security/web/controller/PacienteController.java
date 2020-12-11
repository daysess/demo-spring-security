package com.mballem.curso.security.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mballem.curso.security.domain.Paciente;
import com.mballem.curso.security.domain.Usuario;
import com.mballem.curso.security.service.PacienteService;
import com.mballem.curso.security.service.UsuarioService;

@Controller
@RequestMapping("pacientes")
public class PacienteController {
	
	@Autowired
	private PacienteService pacienteService;
	
	@Autowired
	private UsuarioService usuarioService;
	
	// abrir pagina dados pessoais do paciente
	@GetMapping("/dados")
	public String cadastrar(Paciente paciente, ModelMap model, @AuthenticationPrincipal User user) {
		paciente = pacienteService.buscarPorUsuarioEmail(user.getUsername());
		if(paciente.hasNotId()) {
			Usuario usuario = new Usuario();
			usuario.setEmail(user.getUsername());
			paciente.setUsuario(usuario);
		}
		model.addAttribute("paciente", paciente);
		return "paciente/cadastro";
	}
	
	// salvar o form de dados pessoais do paciente com verificacao de senha
	@PostMapping("/salvar")
	public String salvar(Paciente paciente, ModelMap model, @AuthenticationPrincipal User user) {
		Usuario u = usuarioService.buscarPorEmail(user.getUsername());
		if(usuarioService.isSenhaCorreta(paciente.getUsuario().getSenha(), u.getSenha())) {
			paciente.setUsuario(u);
			pacienteService.salvar(paciente);
			model.addAttribute("sucesso","Seus dados foram inseridos com sucesso.");
		}else {
			model.addAttribute("falha","Sua senha não confere, tente novamente.");
		}
		return "paciente/cadastro";
	}
	
	// editar o form de dados pessoais do paciente com verificacao de senha
		@PostMapping("/editar")
		public String editar(Paciente paciente, ModelMap model, @AuthenticationPrincipal User user) {
			Usuario u = usuarioService.buscarPorEmail(user.getUsername());
			if(usuarioService.isSenhaCorreta(paciente.getUsuario().getSenha(), u.getSenha())) {
				paciente.setUsuario(u);
				pacienteService.editar(paciente);
				model.addAttribute("sucesso","Seus dados foram editados com sucesso.");
			}else {
				model.addAttribute("falha","Sua senha não confere, tente novamente.");
			}
			return "paciente/cadastro";
		}

}
