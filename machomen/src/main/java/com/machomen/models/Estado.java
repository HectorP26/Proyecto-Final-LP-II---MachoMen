package com.machomen.models;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_estados")
public class Estado {

    @Id
    @Column(name = "idestado")
    private Integer idestado;

    @Column(name = "descripcion")
    private String descripcion;
}
