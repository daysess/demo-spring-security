package com.mballem.curso.security.web.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mballem.curso.security.domain.Medico;
import com.mballem.curso.security.domain.Perfil;
import com.mballem.curso.security.domain.PerfilTipo;
import com.mballem.curso.security.domain.Usuario;
import com.mballem.curso.security.service.MedicoService;
import com.mballem.curso.security.service.UsuarioService;

@Controller
@RequestMapping("u")
public class UsuarioController {
	
	@Autowired
	private UsuarioService usuarioService;
	@Autowired
	private MedicoService medicoService;

	// abrir cadastro de usuarios (medico/admin/paciente)
	@GetMapping({ "/novo/cadastro/usuario" })
	public String cadastroPorAdminParaAdminMedicoPaciente(Usuario usuario) {
		return "usuario/cadastro";
	}
	
	// lista de usuarios
	@GetMapping("/lista")
	public String listarUsuarios() {
		return "usuario/lista";
	}
	
	// lista de usuarios na datatables
	@GetMapping("/datatables/server/usuarios")
	public ResponseEntity<?> listarUsuariosDatatables(HttpServletRequest request) {
		Map<String, Object> lista = usuarioService.buscarTodos(request);
		return ResponseEntity.ok(lista);
	}
	
	// salvar cadastro de usuarios por administrador
	@PostMapping("/cadastro/salvar")
	public String salvarUsuarios(Usuario usuario, RedirectAttributes attr) {
		
		List<Perfil> perfis = usuario.getPerfis();
		if(perfis.size() > 2 ||
				perfis.containsAll(Arrays.asList(new Perfil(1L), new Perfil(3L))) ||
				perfis.containsAll(Arrays.asList(new Perfil(2L), new Perfil(3L))) ) {
			attr.addFlashAttribute("falha", "Paciente não pode ser Admin e/ou Médico");
			attr.addFlashAttribute("usuario", usuario);
		}else {
			try {
				usuarioService.salvarUsuario(usuario);
				attr.addFlashAttribute("sucesso", "Operação realizada com sucesso.");
			} catch (DataIntegrityViolationException ex) {
				attr.addFlashAttribute("falha", "Cadastro não realizado, email já existente.");
			}
			
		}
		return "redirect:/u/novo/cadastro/usuario";
	}
		
	@GetMapping("/editar/credenciais/usuario/{id}")
	public ModelAndView preEditarCredenciais(@PathVariable("id") Long id) {
		Usuario usuario = usuarioService.buscarPorId(id);
		return new ModelAndView("usuario/cadastro", "usuario", usuario);
	}
	
	@GetMapping("/editar/dados/usuario/{id}/perfis/{perfis}")
	public ModelAndView preEditarCadastroDadosPessoais(@PathVariable("id") Long usuarioId,
													   @PathVariable("perfis") Long[] perfisId) {

			Usuario usuario = usuarioService.buscarPorIdEPerfis(usuarioId, perfisId);
			
			if(usuario.getPerfis().contains(new Perfil(PerfilTipo.ADMIN.getCod())) &&
				!usuario.getPerfis().contains(new Perfil(PerfilTipo.MEDICO.getCod()))	) {
				return new ModelAndView("usuario/cadastro", "usuario", usuario);
			}else if(usuario.getPerfis().contains(new Perfil(PerfilTipo.MEDICO.getCod()))) {
				Medico medico = medicoService.buscarPorUsuarioId(usuarioId);
				return medico.hasNotId() 
						? new ModelAndView("medico/cadastro", "medico", new Medico(new Usuario(usuarioId)))
						: new ModelAndView("medico/cadastro", "medico", medico);
			}else if(usuario.getPerfis().contains(new Perfil(PerfilTipo.PACIENTE.getCod()))) {
				ModelAndView model = new ModelAndView("error");
				model.addObject("status", 403);
				model.addObject("error", "Área restrita");
				model.addObject("message","Os dados de pacientes são restritos a ele.");
				return model;
			}
		
		return new ModelAndView("redirect:/u/lista");
	}
	
	@GetMapping("/editar/senha")
	public String abrirEditarSenha() {
		
		return "usuario/editar-senha";
	}
	
	@GetMapping("/confirmar/senha")
	public String editarSenha(@RequestParam("senha1") String s1,
							  @RequestParam("senha2") String s2,
							  @RequestParam("senha3") String s3,
							  @AuthenticationPrincipal User user,
							  RedirectAttributes attr) {
		
		if(!s1.equals(s2)) {
			attr.addAttribute("falha", "Senhas não conferem, tente novamente.");
			return "redirect:/u/editar/senha";
		}
		
		Usuario u = usuarioService.buscarPorEmail(user.getUsername());
		if(!usuarioService.isSenhaCorreta(s3, u.getSenha())) {
			attr.addAttribute("falha", "Senha atual não confere, tente novamente.");
			return "redirect:/u/editar/senha";
		}
		
		usuarioService.alterarSenha(u,s1);
		attr.addAttribute("sucesso", "Senha alterada com sucesso.");
		return "redirect:/u/editar/senha";
	}


}
