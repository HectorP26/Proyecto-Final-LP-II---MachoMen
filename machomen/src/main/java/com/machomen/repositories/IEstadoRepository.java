package com.machomen.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.machomen.models.Estado;

public interface IEstadoRepository extends JpaRepository<Estado, Integer> {
}