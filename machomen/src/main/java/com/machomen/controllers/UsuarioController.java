package com.machomen.controllers;

import com.machomen.models.Usuario;
import com.machomen.repositories.*;
import com.machomen.utils.Alert;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private IUsuarioRepository usuarioRepo;

    @Autowired
    private ITIpoRepository tipoRepo;

    @Autowired
    private IEstadoRepository estadoRepo;

    // Listado con filtros
    @GetMapping("/listado")
    public String listado(Model model,
                          @RequestParam(name = "tipo", defaultValue = "0") int tipo,
                          @RequestParam(name = "busqueda", defaultValue = "") String busqueda) {

        List<Usuario> lista;

        if (tipo != 0 && !busqueda.isBlank()) {
            lista = usuarioRepo.findByTipoIdtipoAndNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(
                    tipo, busqueda, busqueda);
        } else if (tipo != 0) {
            lista = usuarioRepo.findByTipoIdtipo(tipo);
        } else if (!busqueda.isBlank()) {
            lista = usuarioRepo.findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(busqueda, busqueda);
        } else {
            lista = usuarioRepo.findAll();
        }

        model.addAttribute("tipos", tipoRepo.findAll());
        model.addAttribute("lista", lista);
        model.addAttribute("tipoSeleccionado", tipo);
        model.addAttribute("busqueda", busqueda);

        return "usuarios/listado";
    }

    // 📄 Formulario nuevo usuario
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("usuario", new Usuario());
        cargarListas(model);
        return "usuarios/nuevo";
    }

    // ✅ Registrar usuario
    @PostMapping("/registrar")
    public String registrar(@Valid @ModelAttribute Usuario usuario,
                            BindingResult result,
                            Model model,
                            RedirectAttributes flash) {
        if (result.hasErrors()) {
            cargarListas(model);
            return "usuarios/nuevo";
        }

        usuario.setTipo(tipoRepo.findById(usuario.getTipo().getIdtipo()).orElse(null));
        usuario.setEstado(estadoRepo.findById(usuario.getEstado().getIdestado()).orElse(null));

        // Se guarda la clave tal como la ingresó (sin cifrar)
        usuarioRepo.save(usuario);

        flash.addFlashAttribute("alert", Alert.sweetAlertSuccess("Usuario registrado correctamente."));
        return "redirect:/usuarios/listado";
    }

    // ✏️ Editar usuario
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        Usuario usuario = usuarioRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));
        model.addAttribute("usuario", usuario);
        cargarListas(model);
        return "usuarios/editar";
    }

    // 💾 Guardar edición
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Usuario usuario,
                          BindingResult result,
                          Model model,
                          RedirectAttributes flash) {
        if (result.hasErrors()) {
            cargarListas(model);
            return "usuarios/editar";
        }

        if (usuario.getClave() == null || usuario.getClave().isBlank()) {
            Usuario original = usuarioRepo.findById(usuario.getCodigo())
                    .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + usuario.getCodigo()));
            usuario.setClave(original.getClave());
        }

        usuario.setTipo(tipoRepo.findById(usuario.getTipo().getIdtipo()).orElse(null));
        usuario.setEstado(estadoRepo.findById(usuario.getEstado().getIdestado()).orElse(null));

        usuarioRepo.save(usuario);
        flash.addFlashAttribute("alert", Alert.sweetAlertSuccess("Usuario actualizado correctamente."));
        return "redirect:/usuarios/listado";
    }

    // 🗑️ Eliminar usuario
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, RedirectAttributes flash) {
        Usuario usuario = usuarioRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));
        usuarioRepo.delete(usuario);
        flash.addFlashAttribute("alert", Alert.sweetAlertSuccess("Usuario eliminado."));
        return "redirect:/usuarios/listado";
    }

    // 📋 Cargar combos
    private void cargarListas(Model model) {
        model.addAttribute("tipos", tipoRepo.findAll());
        model.addAttribute("estados", estadoRepo.findAll());
    }

    // 👤 Ver y editar perfil del usuario logueado
    @GetMapping("/perfil")
    public String perfilUsuario(Model model, HttpSession session) {
        Integer idUsuario = (Integer) session.getAttribute("idUsuario");
        if (idUsuario == null) {
            return "redirect:/login";
        }

        Usuario usuario = usuarioRepo.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        model.addAttribute("usuario", usuario);
        cargarListas(model); // Solo si se editan tipo/estado

        return "usuarios/perfil";
    }

    @PostMapping("/perfil")
    public String actualizarPerfil(@Valid @ModelAttribute("usuario") Usuario usuario,
                                   BindingResult result,
                                   Model model,
                                   HttpSession session) {

        if (result.hasErrors()) {
            model.addAttribute("alert", Alert.sweetAlertError("Por favor, corrige los errores del formulario."));
            return "usuarios/perfil";
        }

        if (usuario.getClave() == null || usuario.getClave().isBlank()) {
            Usuario original = usuarioRepo.findById(usuario.getCodigo())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
            usuario.setClave(original.getClave());
        }

        usuario.setTipo(tipoRepo.findById(usuario.getTipo().getIdtipo()).orElse(null));
        usuario.setEstado(estadoRepo.findById(usuario.getEstado().getIdestado()).orElse(null));

        usuarioRepo.save(usuario);

        // Actualizar el nombre en sesión por si cambió
        String nuevoNombreCompleto = usuario.getNombre() + " " + usuario.getApellido();
        session.setAttribute("nombreCompleto", nuevoNombreCompleto);

        model.addAttribute("alert", Alert.sweetAlertSuccess("Perfil actualizado correctamente."));
        model.addAttribute("usuario", usuario); // volver a cargar datos

        return "usuarios/perfil";
    }
}
