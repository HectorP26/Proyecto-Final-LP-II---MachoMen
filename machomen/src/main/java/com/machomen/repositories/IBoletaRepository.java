package com.machomen.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.machomen.models.Boleta;
import com.machomen.models.Usuario;

public interface IBoletaRepository extends JpaRepository<Boleta, Integer> {

	List<Boleta> findAllByOrderByNumBoletaDesc();
	
	List<Boleta> findByUsuarioOrderByFechaDesc(Usuario usuario);
	
	

	    @Query("SELECT b FROM Boleta b " +
	           "LEFT JOIN FETCH b.lstDetalleBoleta d " +
	           "LEFT JOIN FETCH d.producto " +
	           "WHERE b.numBoleta = :numBoleta")
	    Boleta findByNumBoletaFetchDetalle(@Param("numBoleta") Integer numBoleta);
	

	
}
