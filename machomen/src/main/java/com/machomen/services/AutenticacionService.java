package com.machomen.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.machomen.dtos.AutenticacionFilter;
import com.machomen.models.Usuario;
import com.machomen.repositories.IUsuarioRepository;

@Service
public class AutenticacionService {
	
	@Autowired
	private IUsuarioRepository _usuarioRepository;
	
	public Usuario autenticar(AutenticacionFilter filter) {
		return _usuarioRepository.findByCorreoAndClave(filter.getCorreo(), filter.getPassword());

	}

}
