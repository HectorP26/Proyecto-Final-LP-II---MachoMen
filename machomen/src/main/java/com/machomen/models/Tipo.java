package com.machomen.models;




import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "tb_tipos")
@Data
public class Tipo {
    @Id
    @Column(name = "idtipo")
    private Integer idtipo;

    @Column(name = "descripcion")
    private String descripcion;
}

