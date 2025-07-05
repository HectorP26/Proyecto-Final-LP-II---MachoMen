package com.machomen.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.machomen.dtos.ProductoFilter;
import com.machomen.dtos.ResultadoResponse;
import com.machomen.models.Boleta;
import com.machomen.models.DetalleBoleta;
import com.machomen.models.Producto;
import com.machomen.models.Usuario;
import com.machomen.repositories.IBoletaRepository;
import com.machomen.repositories.IProductoRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class BoletaService {

    @Autowired
    private IBoletaRepository boletaRepository;

    @Autowired
    private IProductoRepository productoRepository;

    public List<Boleta> listarBoletas() {
        return boletaRepository.findAllByOrderByNumBoletaDesc();
    }

    public ResultadoResponse registrarBoleta(Boleta boleta) {
        try {
            StringBuilder errores = new StringBuilder();

            for (DetalleBoleta detalle : boleta.getLstDetalleBoleta()) {
                String idProducto = detalle.getProducto().getIdProd();
                Producto prod = productoRepository.findById(idProducto).orElse(null);

                if (prod == null) {
                    errores.append(String.format("Producto %s no encontrado<br>", idProducto));
                    continue;
                }

                if (detalle.getCantidad() > prod.getStkProd()) {
                    errores.append(String.format("Stock insuficiente para %s<br>", prod.getDesProd()));
                }
            }

            if (errores.length() > 0) {
                return new ResultadoResponse(false, errores.toString());
            }

            for (DetalleBoleta detalle : boleta.getLstDetalleBoleta()) {
                Producto prod = productoRepository.findById(detalle.getProducto().getIdProd()).orElse(null);
                if (prod != null) {
                    prod.setStkProd(prod.getStkProd() - detalle.getCantidad());
                    productoRepository.save(prod);
                }
            }

            boletaRepository.save(boleta);
            return new ResultadoResponse(true, "Boleta registrada correctamente");

        } catch (Exception e) {
            e.printStackTrace();
            return new ResultadoResponse(false, "Error al registrar: " + e.getMessage());
        }
    }

    // 🔍 Método para filtrar boletas por categoría y/o texto
    public List<Boleta> filtrarBoletas(ProductoFilter filtro) {
        List<Boleta> todas = listarBoletas();

        return todas.stream()
            .filter(boleta -> {
                boolean coincideCategoria = true;
                boolean coincideBusqueda = true;

                if (filtro.getIdCategoria() != null && filtro.getIdCategoria() != 0) {
                    coincideCategoria = boleta.getLstDetalleBoleta().stream()
                        .anyMatch(detalle -> detalle.getProducto().getCategoria().getIdCategoria() == filtro.getIdCategoria());
                }

                if (filtro.getBusqueda() != null && !filtro.getBusqueda().trim().isEmpty()) {
                    String busqueda = filtro.getBusqueda().toLowerCase();
                    coincideBusqueda = boleta.getLstDetalleBoleta().stream()
                        .anyMatch(detalle -> detalle.getProducto().getDesProd().toLowerCase().contains(busqueda));
                }

                return coincideCategoria && coincideBusqueda;
            })
            .collect(Collectors.toList());
    }
    
    public Boleta getOne(Integer id) {
        return boletaRepository.findById(id).orElse(null);
    }
    
    public List<Boleta> listarPorUsuario(Usuario usuario) {
        return boletaRepository.findByUsuarioOrderByFechaDesc(usuario);
    }
    
    public Boleta getByNumBoleta(Integer numBoleta) {
        return boletaRepository.findByNumBoletaFetchDetalle(numBoleta);
    }

}
