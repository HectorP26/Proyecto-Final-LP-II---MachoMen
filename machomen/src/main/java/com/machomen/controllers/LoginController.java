package com.machomen.controllers;

import com.machomen.dtos.AutenticacionFilter;
import com.machomen.models.Usuario;
import com.machomen.services.AutenticacionService;
import com.machomen.utils.Alert;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    @Autowired
    private AutenticacionService autenticacionService;

    // Mostrar formulario de login
    @GetMapping({ "/", "/login" })
    public String mostrarLogin(Model model) {
        model.addAttribute("filter", new AutenticacionFilter());
        return "login";
    }

    // Procesar el login
    @PostMapping("/iniciar-sesion")
    public String iniciarSesion(
            @Valid @ModelAttribute("filter") AutenticacionFilter filter,
            BindingResult bindingResult,
            HttpSession session,
            Model model,
            RedirectAttributes flash) {

        // Validaciones del formulario
        if (bindingResult.hasErrors()) {
            return "login";
        }

        // Autenticar usuario
        Usuario usuarioValidado = autenticacionService.autenticar(filter);

        // Si no es válido, regresar al login con alerta
        if (usuarioValidado == null) {
            model.addAttribute("alert", Alert.sweetAlertError("Usuario y/o clave incorrecta"));
            model.addAttribute("filter", new AutenticacionFilter());
            return "login";
        }

        // Crear variables de sesión
        String nombreCompleto = usuarioValidado.getNombre() + " " + usuarioValidado.getApellido();
        session.setAttribute("idUsuario", usuarioValidado.getCodigo());
        session.setAttribute("nombreCompleto", nombreCompleto);
        session.setAttribute("cuenta", usuarioValidado.getCorreo());

        // Determinar rol
        String rolSesion = (usuarioValidado.getTipo() != null &&
                "administrativo".equalsIgnoreCase(usuarioValidado.getTipo().getDescripcion()))
                ? "ADMIN"
                : "USER";
        session.setAttribute("rol", rolSesion);

        // Seleccionar imagen diferente para cada rol
        String imagenBienvenida = rolSesion.equals("ADMIN")
                ? "/img/admin/ernesto.png"
                : "/img/cliente/cristiano.png";

        // Crear alerta con imagen
        String mensaje = Alert.sweetImageUrl("Bienvenido a MachoMen", nombreCompleto, imagenBienvenida);
        flash.addFlashAttribute("alert", mensaje);

        // Redirigir al menú principal
        return "redirect:/menuprincipal";
    }

    // Menú principal
    @GetMapping("/menuprincipal")
    public String mostrarMenuPrincipal(HttpSession session, Model model) {
        model.addAttribute("rol", session.getAttribute("rol"));
        model.addAttribute("nombreCompleto", session.getAttribute("nombreCompleto"));
        return "menuprincipal";
    }

    // Cerrar sesión
    @GetMapping("/cerrar-sesion")
    public String cerrarSesion(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
