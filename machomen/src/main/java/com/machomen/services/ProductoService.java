package com.machomen.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.machomen.dtos.ProductoFilter;
import com.machomen.dtos.ResultadoResponse;
import com.machomen.models.Estado;
import com.machomen.models.Producto;
import com.machomen.models.Categoria;
import com.machomen.repositories.IProductoRepository;
import com.machomen.repositories.IEstadoRepository;
import com.machomen.repositories.ICategoriaRepository;

@Service
public class ProductoService {

    @Autowired
    private IProductoRepository productoRepo;

    @Autowired
    private IEstadoRepository estadoRepo;

    @Autowired
    private ICategoriaRepository categoriaRepo;

    // Obtener todos los productos ordenados por ID
    public List<Producto> getAll() {
        return productoRepo.findAllByOrderByIdProdAsc();
    }

    // Obtener productos activos
    public List<Producto> getActivos() {
        return productoRepo.findByEstado_Idestado(1); // 1 es "activo" según tu tabla tb_estados
    }

    // Buscar productos por categoría
    public List<Producto> search(ProductoFilter filtro) {
        if (filtro.getIdCategoria() != null && filtro.getIdCategoria() != 0) {
            return productoRepo.findByCategoriaIdCategoria(filtro.getIdCategoria());
        }
        return productoRepo.findAll();
    }

    // Registrar nuevo producto
    public ResultadoResponse create(Producto producto) {
        try {
            Estado estadoActivo = estadoRepo.findById(1).orElse(null);
            if (estadoActivo == null) {
                return new ResultadoResponse(false, "El estado 'Activo' no está disponible.");
            }
            producto.setEstado(estadoActivo);

            Producto registrado = productoRepo.save(producto);
            String mensaje = String.format("Producto con código %s registrado exitosamente.", registrado.getIdProd());
            return new ResultadoResponse(true, mensaje);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResultadoResponse(false, "Error al registrar el producto: " + ex.getMessage());
        }
    }

    // Obtener producto por ID
    public Producto getOne(String id) {
        return productoRepo.findById(id).orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }

    // Actualizar producto
    public ResultadoResponse update(Producto producto) {
        try {
            Producto actualizado = productoRepo.save(producto);
            String mensaje = String.format("Producto con código %s actualizado correctamente.", actualizado.getIdProd());
            return new ResultadoResponse(true, mensaje);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResultadoResponse(false, "Error al actualizar el producto: " + ex.getMessage());
        }
    }

    // Cambiar estado entre Activo (1) e Inactivo (2)
    public ResultadoResponse cambiarEstado(String id) {
        try {
            Producto producto = this.getOne(id);
            int estadoActualId = producto.getEstado().getIdestado();
            int nuevoEstadoId = (estadoActualId == 1) ? 2 : 1;

            Estado nuevoEstado = estadoRepo.findById(nuevoEstadoId)
                .orElseThrow(() -> new RuntimeException("Estado no encontrado para el ID: " + nuevoEstadoId));

            producto.setEstado(nuevoEstado);
            productoRepo.save(producto);

            String accion = (nuevoEstadoId == 1) ? "activado" : "desactivado";
            String mensaje = String.format("Producto con código %s %s.", producto.getIdProd(), accion);
            return new ResultadoResponse(true, mensaje);

        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResultadoResponse(false, "Error al cambiar el estado: " + ex.getMessage());
        }
    }

    // Filtrar productos por categoría y/o búsqueda
    public List<Producto> filtrarProductos(Integer idCategoria, String busqueda) {
        if ((idCategoria == null || idCategoria == 0) && (busqueda == null || busqueda.trim().isEmpty())) {
            return productoRepo.findAll();
        }

        return productoRepo.buscarPorCategoriaYDescripcion(
            idCategoria == 0 ? null : idCategoria,
            (busqueda == null) ? "" : busqueda.trim()
        );
    }

    // ✅ Nuevo método para obtener todas las categorías
    public List<Categoria> getCategorias() {
        return categoriaRepo.findAll();
    }

    public ResultadoResponse actualizarStock(String idProd, int cantidad) {
        try {
            Producto producto = this.getOne(idProd);
            int stockActual = producto.getStkProd();

            if (stockActual < cantidad) {
                return new ResultadoResponse(false, "Stock insuficiente para el producto: " + producto.getDesProd());
            }

            producto.setStkProd(stockActual - cantidad);
            productoRepo.save(producto);

            return new ResultadoResponse(true, "Stock actualizado correctamente para el producto: " + producto.getDesProd());
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResultadoResponse(false, "Error al actualizar stock: " + ex.getMessage());
        }
    }

}
