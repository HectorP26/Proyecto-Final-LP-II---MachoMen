package com.machomen.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.machomen.models.Producto;

public interface IProductoRepository extends JpaRepository<Producto, String> {

    List<Producto> findAllByOrderByIdProdAsc();

    List<Producto> findByCategoriaIdCategoria(int id);

    List<Producto> findByDesProdContainingIgnoreCase(String desProd);

    // ✅ Nuevo método para obtener el último producto (mayor ID)
    @Query(value = "SELECT * FROM tb_productos ORDER BY id_prod DESC LIMIT 1", nativeQuery = true)
    Producto obtenerUltimoProducto();
    
    List<Producto> findByEstado_Idestado(Integer idestado);

    @Query("""
    	    SELECT p FROM Producto p
    	    WHERE (:idCategoria IS NULL OR p.categoria.idCategoria = :idCategoria)
    	    AND (LOWER(p.desProd) LIKE LOWER(CONCAT('%', :busqueda, '%')))
    	    ORDER BY p.idProd DESC
    	""")
    	List<Producto> buscarPorCategoriaYDescripcion(
    	    @Param("idCategoria") Integer idCategoria,
    	    @Param("busqueda") String busqueda
    	);

}
