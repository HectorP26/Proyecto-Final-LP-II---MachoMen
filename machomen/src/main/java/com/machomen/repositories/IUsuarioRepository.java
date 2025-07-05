package com.machomen.repositories;

import com.machomen.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IUsuarioRepository extends JpaRepository<Usuario, Integer> {

    // 🔐 Login (puedes mejorar luego con BCrypt y compare)
    Usuario findByCorreoAndClave(String correo, String clave);

    // 🔍 Filtrar solo por tipo (cliente o administrador)
    List<Usuario> findByTipoIdtipo(int idtipo);

    // 🔍 Buscar por nombre o apellido
    List<Usuario> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(String nombre, String apellido);

    // 🔍 Filtrar por tipo Y buscar por nombre o apellido (combinado)
    List<Usuario> findByTipoIdtipoAndNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(
            int idtipo, String nombre, String apellido
    );
}