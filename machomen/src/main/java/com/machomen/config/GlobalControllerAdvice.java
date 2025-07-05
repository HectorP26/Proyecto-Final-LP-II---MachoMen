package com.machomen.config;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

@ControllerAdvice  // <-- Aquí el cambio importante
public class GlobalControllerAdvice {

    @ModelAttribute
    public void addAttributes(HttpSession session, Model model) {
        Object nombreCompleto = session.getAttribute("nombreCompleto");
        Object rol = session.getAttribute("rol");
        if (nombreCompleto != null) {
            model.addAttribute("nombreCompleto", nombreCompleto);
        }
        if (rol != null) {
            model.addAttribute("rol", rol);
        }
    }
}
