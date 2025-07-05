package com.machomen.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

@Controller
@RequestMapping("/carrito")
@SessionAttributes("carrito")
public class CarritoController {

  @Autowired private ProductoService productoService;
  @Autowired private UsuarioService usuarioService;
  @Autowired private BoletaService boletaService;

  @ModelAttribute("carrito")
  public List<ProductoSeleccionado> iniciarCarrito() {
    return new ArrayList<>();
  }

  // Agregar producto al carrito
  @PostMapping("/agregar")
  public String agregar(@RequestParam String idProd,
                        @RequestParam int cantidad,
                        @ModelAttribute("carrito") List<ProductoSeleccionado> carrito,
                        RedirectAttributes flash,
                        HttpSession session) {

    Producto prod = productoService.getOne(idProd);
    if (prod == null) {
      flash.addFlashAttribute("error", "Producto no encontrado");
      return "redirect:/catalogo";
    }

    if (cantidad < 1 || cantidad > prod.getStkProd()) {
      flash.addFlashAttribute("error", "Cantidad inválida");
      return "redirect:/mostrar?codigo=" + idProd;
    }

    boolean existe = carrito.stream().anyMatch(p -> p.getIdProd().equals(idProd));
    if (existe) {
      flash.addFlashAttribute("info", "Producto ya en carrito");
      return "redirect:/carrito/ver";
    }

    ProductoSeleccionado sel = new ProductoSeleccionado();
    sel.setIdProd(idProd);
    sel.setDescripcion(prod.getDesProd());
    sel.setPrecio(prod.getPreProd().doubleValue());
    sel.setCantidad(cantidad);
   
    carrito.add(sel);

    // Actualiza cantidad total de productos en el ícono
    session.setAttribute("carritoSize", carrito.size());

    flash.addFlashAttribute("toast", Alert.sweetToast("Producto agregado al carrito", "success", 2000));
    return "redirect:/carrito/ver";
  }

  // Ver el carrito
  @GetMapping("/ver")
  public String verCarrito(Model model,
                           @ModelAttribute("carrito") List<ProductoSeleccionado> carrito,
                           HttpSession session) {

    Integer idUsuario = (Integer) session.getAttribute("idUsuario");
    if (idUsuario == null) {
        return "redirect:/login";
    }

    Usuario usuario = usuarioService.getOne(idUsuario);
    List<Boleta> boletasDelUsuario = boletaService.listarPorUsuario(usuario);
    model.addAttribute("boletas", boletasDelUsuario);

    double totalCarrito = carrito.stream()
    .mapToDouble(p -> p.getCantidad() * p.getPrecio())
    .sum();
model.addAttribute("totalCarrito", totalCarrito);

    LocalDate fecha = LocalDate.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    model.addAttribute("fechaFormateada", fecha.format(formatter));

    return "carrito/ver";
  }

  // Registrar compra
  @PostMapping("/registrar")
  public String registrarDesdeCarrito(@ModelAttribute("carrito") List<ProductoSeleccionado> carrito,
                                      HttpSession session,
                                      RedirectAttributes flash,
                                      SessionStatus status) {

    Integer idUsuario = (Integer) session.getAttribute("idUsuario");
    if (idUsuario == null) {
        flash.addFlashAttribute("alert", Alert.sweetAlertError("Sesión expirada"));
        return "redirect:/login";
    }

    if (carrito == null || carrito.isEmpty()) {
        flash.addFlashAttribute("alert", Alert.sweetAlertInfo("El carrito está vacío"));
        return "redirect:/carrito/ver";
    }

    Usuario usuario = usuarioService.getOne(idUsuario);
    LocalDate fechaActual = LocalDate.now();

    for (ProductoSeleccionado item : carrito) {
        ResultadoResponse res = productoService.actualizarStock(item.getIdProd(), item.getCantidad());
        if (!res.isSuccess()) {
            flash.addFlashAttribute("alert", Alert.sweetAlertError(res.getMensaje()));
            return "redirect:/carrito/ver";
        }
    }

    Boleta boleta = new Boleta();
    boleta.setUsuario(usuario);
    boleta.setFecha(fechaActual);

    List<DetalleBoleta> detalles = new ArrayList<>();
    for (ProductoSeleccionado item : carrito) {
        Producto prod = productoService.getOne(item.getIdProd());
        prod.setStkProd(prod.getStkProd() - item.getCantidad());
        productoService.update(prod);

        DetalleBoleta detalle = new DetalleBoleta(boleta, prod, item.getCantidad(), item.getPrecio());
        detalles.add(detalle);
    }
    boleta.setLstDetalleBoleta(detalles);

    try {
        ResultadoResponse response = boletaService.registrarBoleta(boleta);
        if (!response.isSuccess()) {
            flash.addFlashAttribute("alert", Alert.sweetAlertErrorHtml(response.getMensaje()));
            return "redirect:/carrito/ver";
        }

        status.setComplete();
        session.setAttribute("carritoSize", 0); // Limpiar contador

        List<Boleta> boletasDelUsuario = boletaService.listarPorUsuario(usuario);
        session.setAttribute("boletas", boletasDelUsuario);

        flash.addFlashAttribute("toast", Alert.sweetAlertSuccess("Compra realizada correctamente."));
        return "redirect:/carrito/ver";

    } catch (Exception ex) {
        flash.addFlashAttribute("alert", Alert.sweetAlertError("Error: " + ex.getMessage()));
        return "redirect:/carrito/ver";
    }
  }
  
  @GetMapping("/eliminar-todo")
  public String eliminarTodoCarrito(HttpSession session, RedirectAttributes flash) {
      session.removeAttribute("carrito");
      session.setAttribute("carritoSize", 0);
      flash.addFlashAttribute("toast", Alert.sweetAlertSuccess("Carrito eliminado correctamente"));
      return "redirect:/carrito/ver";
      
      
  }

  @GetMapping("/eliminar")
public String eliminarProducto(@RequestParam("id") String idProd,
                               @ModelAttribute("carrito") List<ProductoSeleccionado> carrito,
                               HttpSession session,
                               RedirectAttributes flash) {

    carrito.removeIf(p -> p.getIdProd().equals(idProd));

    // Recalcula la cantidad total en el ícono
    int totalItems = carrito.stream().mapToInt(ProductoSeleccionado::getCantidad).sum();
    session.setAttribute("carritoSize", totalItems);

    flash.addFlashAttribute("toast", Alert.sweetToast("Producto eliminado del carrito", "info", 2000));
    return "redirect:/carrito/ver";
}


}
