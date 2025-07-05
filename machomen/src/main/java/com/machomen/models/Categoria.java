package com.machomen.models;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_categorias")
public class Categoria {

    @Id
    @Column(name = "idcategoria")
    private int idCategoria;

    @Column(name = "descripcion", length = 45)
    private String descripcion;
}
