package com.machomen.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.machomen.models.Usuario;
import com.machomen.repositories.IEstadoRepository;
import com.machomen.repositories.ITIpoRepository;
import com.machomen.repositories.IUsuarioRepository;
import com.machomen.utils.Alert;



@Controller
public class RegistroController {

    @Autowired
    private IUsuarioRepository usuarioRepo;

    @Autowired
    private ITIpoRepository tipoRepo;

    @Autowired
    private IEstadoRepository estadoRepo;

    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro"; 
    }

    @PostMapping("/registro")
    public String registrarUsuario(
            @ModelAttribute("usuario") Usuario usuario,
            BindingResult result,
            Model model,
            RedirectAttributes flash) {

        // Asigna tipo y estado ANTES de validar
        usuario.setTipo(tipoRepo.findById(2).orElse(null));
        usuario.setEstado(estadoRepo.findById(1).orElse(null));

        // Ahora sí, valida
        if (usuario.getTipo() == null || usuario.getEstado() == null) {
            model.addAttribute("usuario", usuario);
            result.reject("usuario.tipo", "Seleccione un tipo válido");
            result.reject("usuario.estado", "Seleccione un estado válido");
            return "registro";
        }

        // Validar campos básicos como nombre, correo, etc.
        // Si quieres usar un validador, podrías validarlo manualmente o con un servicio extra.

        usuarioRepo.save(usuario);
        flash.addFlashAttribute("alert", Alert.sweetAlertSuccess("¡Registro exitoso!"));
        return "redirect:/login";
    }

}
