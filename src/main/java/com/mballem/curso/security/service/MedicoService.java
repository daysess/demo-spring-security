package com.mballem.curso.security.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mballem.curso.security.domain.Medico;
import com.mballem.curso.security.repository.MedicoRepository;

@Service
public class MedicoService {
	
	@Autowired
	private MedicoRepository medicoRepository;
	
	@Transactional(readOnly = true)
	public Medico buscarPorUsuarioId(Long id) {
		return medicoRepository.findByUsuarioId(id).orElse(new Medico());
	}

	@Transactional(readOnly = false)
	public void salvar(Medico medico) {
		medicoRepository.save(medico);		
	}

	@Transactional(readOnly = false)
	public void editar(Medico medico) {
		Medico m2 = medicoRepository.findById(medico.getId()).get();
		m2.setCrm(medico.getCrm());
		m2.setDtInscricao(medico.getDtInscricao());
		m2.setNome(medico.getNome());
		if(!medico.getEspecialidades().isEmpty()) {
			m2.getEspecialidades().addAll(medico.getEspecialidades());
		}
		
	}

	@Transactional(readOnly = true)
	public Medico buscarPorEmailUsuario(String email) {
		return medicoRepository.findByEmailUsuario(email).orElse(new Medico());
	}

	@Transactional(readOnly = false)
	public void excluirEspecializacaoPorMedico(Long idMedico, Long idEspecializacao) {
		Medico medico = medicoRepository.findById(idMedico).get();
		medico.getEspecialidades().removeIf(e -> e.getId().equals(idEspecializacao));
	}

	@Transactional(readOnly = true)
	public List<Medico> buscarMedicoPorEspecialidade(String titulo) {
		return medicoRepository.findByMedicosPorEspecialidade(titulo);
	}

	@Transactional(readOnly = true)
	public boolean existeEspecialidadeAgendada(Long idMedico, Long idEspecializacao) {
		return medicoRepository.hasEspecialidadeAgendada(idMedico, idEspecializacao).isPresent();
	}

}
