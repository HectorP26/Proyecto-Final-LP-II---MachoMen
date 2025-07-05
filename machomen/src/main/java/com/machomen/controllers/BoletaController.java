package com.machomen.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.machomen.dtos.ProductoFilter;
import com.machomen.dtos.ProductoSeleccionado;
import com.machomen.dtos.ResultadoResponse;
import com.machomen.models.Boleta;
import com.machomen.models.DetalleBoleta;
import com.machomen.models.Producto;
import com.machomen.models.Usuario;
import com.machomen.services.BoletaService;
import com.machomen.services.ProductoService;
import com.machomen.services.UsuarioService;
import com.machomen.utils.Alert;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/boletas")
@SessionAttributes("seleccionados")
public class BoletaController {

    @Autowired
    private BoletaService boletaService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private UsuarioService usuarioService;

    @ModelAttribute("seleccionados")
    public List<ProductoSeleccionado> inicializarSeleccionados() {
        return new ArrayList<>();
    }

    @GetMapping("/filtrado")
    public String filtrado(@ModelAttribute("filtro") ProductoFilter filtro, Model model) {
        List<Boleta> lstBoletas = boletaService.filtrarBoletas(filtro);
        model.addAttribute("lstBoletas", lstBoletas);
        model.addAttribute("categorias", productoService.getCategorias()); 
        model.addAttribute("boleta", new Boleta());
        return "boletas/filtrado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("productos", productoService.getActivos());
        model.addAttribute("productoSeleccionado", new ProductoSeleccionado());
        return "boletas/nuevo";
    }

    @PostMapping("/agregar")
    public String agregar(@Valid @ModelAttribute ProductoSeleccionado seleccionado, BindingResult bindingResult,
                          @ModelAttribute("seleccionados") List<ProductoSeleccionado> seleccionados,
                          Model model) {
        model.addAttribute("productos", productoService.getActivos());

        if (bindingResult.hasErrors()) {
            model.addAttribute("alert", Alert.sweetAlertInfo("Error en datos del producto"));
            return "boletas/nuevo";
        }

        boolean yaExiste = seleccionados.stream()
                .anyMatch(p -> p.getIdProd().equals(seleccionado.getIdProd()));

        if (yaExiste) {
            model.addAttribute("alert", Alert.sweetAlertInfo("Este producto ya fue agregado"));
            return "boletas/nuevo";
        }

        seleccionados.add(seleccionado);
        model.addAttribute("productoSeleccionado", new ProductoSeleccionado());
        return "boletas/nuevo";
    }

    @PostMapping("/quitar")
    public String quitar(@RequestParam String codigo,
                         @ModelAttribute("seleccionados") List<ProductoSeleccionado> seleccionados,
                         Model model) {
        seleccionados.removeIf(p -> p.getIdProd().equals(codigo));
        model.addAttribute("productos", productoService.getActivos());
        model.addAttribute("productoSeleccionado", new ProductoSeleccionado());
        return "boletas/nuevo";
    }

    @PostMapping("/registrar")
    public String registrar(@Valid @ModelAttribute Boleta boleta, BindingResult result,
                            @ModelAttribute("seleccionados") List<ProductoSeleccionado> seleccionados,
                            Model model, HttpSession session, RedirectAttributes flash) {

        model.addAttribute("productos", productoService.getActivos());
        model.addAttribute("productoSeleccionado", new ProductoSeleccionado());

        Integer idUsuario = (Integer) session.getAttribute("idUsuario");
        if (idUsuario == null) {
            flash.addFlashAttribute("alert", Alert.sweetAlertError("Sesión expirada"));
            return "redirect:/login";
        }

        Usuario usuario = usuarioService.getOne(idUsuario);
        boleta.setUsuario(usuario);

        if (seleccionados.isEmpty()) {
            model.addAttribute("alert", Alert.sweetAlertInfo("Agregue al menos un producto"));
            return "boletas/nuevo";
        }

        List<DetalleBoleta> detalles = seleccionados.stream()
                .map(item -> new DetalleBoleta(boleta,
                        productoService.getOne(item.getIdProd()),
                        item.getCantidad(),
                        item.getPrecio()))
                .toList();

        boleta.setLstDetalleBoleta(detalles);

        if (result.hasErrors()) {
            model.addAttribute("alert", Alert.sweetAlertInfo("Datos incompletos"));
            return "boletas/nuevo";
        }

        try {
            ResultadoResponse response = boletaService.registrarBoleta(boleta);
            if (!response.isSuccess()) {
                model.addAttribute("alert", Alert.sweetAlertErrorHtml(response.getMensaje()));
                return "boletas/nuevo";
            }

            flash.addFlashAttribute("toast", Alert.sweetToast(response.getMensaje(), "success", 5000));
            session.removeAttribute("seleccionados");
            return "redirect:/boletas/filtrado";

        } catch (Exception ex) {
            model.addAttribute("alert", Alert.sweetAlertError("Error: " + ex.getMessage()));
            return "boletas/nuevo";
        }
    }
    
    @GetMapping("/mis-boletas")
    public String mostrarMisBoletas(HttpSession session, Model model, RedirectAttributes flash) {
        Integer idUsuario = (Integer) session.getAttribute("idUsuario");
        if (idUsuario == null) {
            flash.addFlashAttribute("alert", Alert.sweetAlertError("Sesión expirada"));
            return "redirect:/login";
        }

        // Obtener el usuario
        Usuario usuario = usuarioService.getOne(idUsuario);
        
        // Obtener las boletas del usuario actual
        List<Boleta> boletasDelUsuario = boletaService.listarPorUsuario(usuario);
        
        // Agregar al modelo para mostrar en la vista
        model.addAttribute("boletas", boletasDelUsuario);
        
        // Retornar la vista que muestra las boletas
        return "boletas/mis-boletas"; // Cambia por la vista que desees
    }

    
    @GetMapping("/{numBoleta}")
    public String verDetalle(@PathVariable Integer numBoleta, Model model, RedirectAttributes flash) {
        Boleta boleta = boletaService.getByNumBoleta(numBoleta);

        if (boleta == null) {
            flash.addFlashAttribute("alert", Alert.sweetAlertError("Boleta no encontrada"));
            return "redirect:/boletas/mias";
        }

        model.addAttribute("boleta", boleta);
        return "boletas/detalle";
    }

    @GetMapping("/mostrar")
    public String mostrarDetalle(@RequestParam("codigo") String codigo, Model model) {
        Producto producto = productoService.getOne(codigo);

        if (producto == null) {
            model.addAttribute("error", "Producto no encontrado");
            return "error";  // Redirige a una página de error o muestra un mensaje adecuado
        }

        model.addAttribute("producto", producto);
        return "boletas/detalle";  // Asegúrate de que el path sea correcto
    }
}
