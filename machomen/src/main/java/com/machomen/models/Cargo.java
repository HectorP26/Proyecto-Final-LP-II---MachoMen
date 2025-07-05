package com.machomen.models;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_cargo")
public class Cargo {

    @Id
    @Column(name = "cargo")
    private int cargo;

    @Column(name = "descripcion", length = 15)
    private String descripcion;
}
