// CategoriasController.java
package com.machomen.controllers;

import com.machomen.models.Categoria;
import com.machomen.repositories.ICategoriaRepository;
import com.machomen.utils.Alert;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/categorias")
public class CategoriasController {

    @Autowired
    private ICategoriaRepository categoriaRepo;

    @GetMapping("/listado")
    public String listado(Model model) {
        model.addAttribute("lista", categoriaRepo.findAll());
        return "categorias/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        Categoria nueva = new Categoria();
        nueva.setIdCategoria(generarSiguienteIdCategoria());
        model.addAttribute("categoria", nueva);
        return "categorias/nuevo";
    }

    @PostMapping("/registrar")
    public String registrar(@ModelAttribute Categoria categoria, RedirectAttributes flash, Model model) {

        // Verificar si ya existe una categoría con el mismo ID
        Optional<Categoria> categoriaExistente = categoriaRepo.findById(categoria.getIdCategoria());
        if (categoriaExistente.isPresent()) {
            model.addAttribute("error", "Ya existe una categoría con el ID " + categoria.getIdCategoria() + ".");
            return "categorias/nuevo";  
        }

        // Validación manual de la descripción
        if (categoria.getDescripcion() == null || categoria.getDescripcion().isEmpty()) {
            model.addAttribute("error", "La descripción de la categoría no puede estar vacía.");
            return "categorias/nuevo";  
        }

        categoriaRepo.save(categoria);
        flash.addFlashAttribute("alert", Alert.sweetAlertSuccess("Categoría registrada con ID " + categoria.getIdCategoria()));
        return "redirect:/categorias/listado";  
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        Categoria categoria = categoriaRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));
        model.addAttribute("categoria", categoria);
        return "categorias/editar";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Categoria categoria, RedirectAttributes flash) {
        categoriaRepo.save(categoria);
        flash.addFlashAttribute("alert", Alert.sweetAlertSuccess("Categoría actualizada con ID " + categoria.getIdCategoria()));
        return "redirect:/categorias/listado";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, RedirectAttributes flash) {
        categoriaRepo.deleteById(id);
        flash.addFlashAttribute("alert", Alert.sweetAlertSuccess("Categoría con ID " + id + " eliminada."));
        return "redirect:/categorias/listado";
    }

    private int generarSiguienteIdCategoria() {
        return categoriaRepo.findAll().stream()
            .mapToInt(Categoria::getIdCategoria)
            .max()
            .orElse(0) + 1;
    }
}
