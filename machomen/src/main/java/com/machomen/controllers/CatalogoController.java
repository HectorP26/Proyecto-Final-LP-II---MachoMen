package com.machomen.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.machomen.models.Producto;
import com.machomen.repositories.IProductoRepository;

@Controller
public class CatalogoController {

    @Autowired
    private IProductoRepository productoRepo;

    // Método privado reutilizable para obtener productos según categoría
    private List<Producto> obtenerProductos(int categoria) {
        return (categoria == 0)
            ? productoRepo.findAll()
            : productoRepo.findByCategoriaIdCategoria(categoria);
    }

    // GET: Mostrar catálogo (con o sin filtro)
    @GetMapping("/catalogo")
    public String mostrarCatalogo(
        Model model, 
        @RequestParam(name = "categoria", defaultValue = "0") int categoria,
        @RequestParam(name = "error", required = false) String error
    ) {
        model.addAttribute("lstProductos", obtenerProductos(categoria));
        model.addAttribute("categoria", categoria);
        model.addAttribute("error", error);

        // Ajustado: apuntar a la vista dentro de /templates/productos/catalogo.html
        return "/catalogo";
    }

    // POST: Procesar formulario de filtro y redirigir con categoría seleccionada
    @PostMapping("/catalogo")
    public String filtrarCatalogo(
        @RequestParam(name = "categoria", defaultValue = "0") int categoria
    ) {
        // Redirige con la categoría como parámetro para aplicar el filtro
        return "redirect:/catalogo?categoria=" + categoria;
    }
    
    @GetMapping("/mostrar")
	public String mostrarDetalle(@RequestParam("codigo") String codigo, Model model) {
		Producto producto = productoRepo.findById(codigo)
				.orElseThrow(() -> new IllegalArgumentException("Código no válido: " + codigo));
		model.addAttribute("producto", producto);
		return "/detalle";
	}
}
