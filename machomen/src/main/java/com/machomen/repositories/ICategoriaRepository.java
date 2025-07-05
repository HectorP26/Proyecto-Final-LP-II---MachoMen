package com.machomen.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.machomen.models.Categoria;

public interface ICategoriaRepository extends JpaRepository<Categoria, Integer> {
}