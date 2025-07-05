package com.machomen.services;

import com.machomen.models.Usuario;
import com.machomen.repositories.IUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;

@Service
public class UsuarioService {

    @Autowired
    private IUsuarioRepository _usuarioRepository;

    public Usuario getOne(Integer id) {
        return _usuarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario con ID " + id + " no encontrado"));
    }
}
