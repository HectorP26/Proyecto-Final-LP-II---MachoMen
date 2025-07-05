package com.machomen.controllers;

import com.machomen.models.Categoria;
import com.machomen.models.Estado;
import com.machomen.models.Producto;
import com.machomen.repositories.ICategoriaRepository;
import com.machomen.repositories.IEstadoRepository;
import com.machomen.repositories.IProductoRepository;
import com.machomen.utils.Alert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/productos")
public class ProductosController {

    @Autowired
    private IProductoRepository productoRepo;

    @Autowired
    private ICategoriaRepository categoriaRepo;

    @Autowired
    private IEstadoRepository estadoRepo;

    // LISTADO CON FILTROS
    @GetMapping("/listado")
    public String listado(Model model,
                          @RequestParam(name = "categoria", defaultValue = "0") int categoriaId,
                          @RequestParam(name = "busqueda", defaultValue = "") String busqueda) {

        model.addAttribute("listaCat", categoriaRepo.findAll());
        model.addAttribute("categoriaSeleccionada", categoriaId);
        model.addAttribute("busqueda", busqueda);
        model.addAttribute("lista", obtenerProductos(categoriaId, busqueda));

        return "productos/listado";
    }

    // FILTRADO INTERNO
    private List<Producto> obtenerProductos(int categoriaId, String busqueda) {
        List<Producto> todos = productoRepo.findAllByOrderByIdProdAsc();
        List<Producto> filtrados = new ArrayList<>();

        for (Producto p : todos) {
            boolean coincideCategoria = categoriaId == 0 || p.getCategoria().getIdCategoria() == categoriaId;
            boolean coincideBusqueda = busqueda.isBlank() ||
                    p.getDesProd().toLowerCase().contains(busqueda.toLowerCase()) ||
                    p.getIdProd().toLowerCase().contains(busqueda.toLowerCase());

            if (coincideCategoria && coincideBusqueda) {
                filtrados.add(p);
            }
        }
        return filtrados;
    }

    // GENERAR CÓDIGO AUTOMÁTICO
    private String generarCodigoProducto() {
        Producto ultimo = productoRepo.obtenerUltimoProducto();
        int numero = (ultimo == null) ? 1 : Integer.parseInt(ultimo.getIdProd().substring(1)) + 1;
        return String.format("P%04d", numero);
    }

    // NUEVO PRODUCTO
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        Producto producto = new Producto();
        producto.setIdProd(generarCodigoProducto());
        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categoriaRepo.findAll());
        return "productos/nuevo";
    }

    // REGISTRAR PRODUCTO
    @PostMapping("/registrar")
    public String registrar(@ModelAttribute Producto producto,
                            @RequestParam("categoriaId") Integer categoriaId,
                            RedirectAttributes flash,
                            Model model) {

        Categoria categoria = categoriaRepo.findById(categoriaId).orElse(null);
        Estado estadoActivo = estadoRepo.findById(1).orElse(null);

        if (categoria == null || estadoActivo == null) {
            model.addAttribute("error", "Error: categoría o estado inválido.");
            model.addAttribute("categorias", categoriaRepo.findAll());
            return "productos/nuevo";
        }

        producto.setCategoria(categoria);
        producto.setEstado(estadoActivo);
        productoRepo.save(producto);

        flash.addFlashAttribute("alert", Alert.sweetAlertSuccess("Producto con código " + producto.getIdProd() + " registrado correctamente."));
        return "redirect:/productos/listado";
    }

    // EDITAR PRODUCTO
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable String id, Model model, RedirectAttributes flash) {
        Producto producto = productoRepo.findById(id).orElse(null);
        if (producto == null) {
            flash.addFlashAttribute("alert", Alert.sweetAlertError("Producto no encontrado."));
            return "redirect:/productos/listado";
        }

        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categoriaRepo.findAll());
        return "productos/editar";
    }

    // GUARDAR CAMBIOS DE PRODUCTO
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Producto producto,
                          @RequestParam("categoriaId") Integer categoriaId,
                          RedirectAttributes flash,
                          Model model) {

        Categoria categoria = categoriaRepo.findById(categoriaId).orElse(null);
        if (categoria == null) {
            model.addAttribute("error", "Categoría inválida.");
            model.addAttribute("producto", producto);
            model.addAttribute("categorias", categoriaRepo.findAll());
            return "productos/editar";
        }

        Producto existente = productoRepo.findById(producto.getIdProd()).orElse(null);
        producto.setEstado((existente != null) ? existente.getEstado() : estadoRepo.findById(1).orElse(null));
        producto.setCategoria(categoria);
        productoRepo.save(producto);

        flash.addFlashAttribute("alert", Alert.sweetAlertSuccess("Producto actualizado correctamente."));
        return "redirect:/productos/listado";
    }

    // ELIMINAR PRODUCTO
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable String id, RedirectAttributes flash) {
        if (productoRepo.existsById(id)) {
            productoRepo.deleteById(id);
            flash.addFlashAttribute("alert", Alert.sweetAlertSuccess("Producto eliminado."));
        } else {
            flash.addFlashAttribute("alert", Alert.sweetAlertError("Producto no encontrado."));
        }
        return "redirect:/productos/listado";
    }

    // CAMBIAR ESTADO DEL PRODUCTO (ACTIVO <-> INACTIVO)
    @PostMapping("/cambiar-estado/{id}")
    public String cambiarEstado(@PathVariable String id, RedirectAttributes flash) {
        Producto producto = productoRepo.findById(id).orElse(null);
        if (producto == null) {
            flash.addFlashAttribute("alert", Alert.sweetAlertError("Producto no encontrado."));
            return "redirect:/productos/listado";
        }

        Estado nuevoEstado = producto.getEstado().getIdestado() == 1
                ? estadoRepo.findById(2).orElse(null)
                : estadoRepo.findById(1).orElse(null);

        if (nuevoEstado == null) {
            flash.addFlashAttribute("alert", Alert.sweetAlertError("Error al cambiar el estado."));
            return "redirect:/productos/listado";
        }

        producto.setEstado(nuevoEstado);
        productoRepo.save(producto);

        String mensaje = (nuevoEstado.getIdestado() == 1)
                ? "Producto activado correctamente."
                : "Producto desactivado correctamente.";

        flash.addFlashAttribute("alert", Alert.sweetAlertSuccess(mensaje));
        return "redirect:/productos/listado";
    }
}
