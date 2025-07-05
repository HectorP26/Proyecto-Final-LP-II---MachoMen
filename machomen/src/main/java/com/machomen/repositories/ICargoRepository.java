package com.machomen.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.machomen.models.Cargo;

public interface ICargoRepository extends JpaRepository<Cargo, Integer> {

}
