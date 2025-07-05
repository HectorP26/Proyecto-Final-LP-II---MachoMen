package com.machomen.models;

import com.machomen.dtos.DetalleBoletaId;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tb_det_boleta")
@IdClass(DetalleBoletaId.class)
@Data 
public class DetalleBoleta {

    @Id
    @ManyToOne
    @JoinColumn(name = "num_bol", nullable = false)
    private Boleta boleta;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_prod", nullable = false)
    private Producto producto;

    @Column(name = "cantidad")
    private Integer cantidad;

    @Column(name = "preciovta")
    private Double precioVenta;
    
    // Constructor vacío necesario para Hibernate
    public DetalleBoleta() {
    }
    
    public DetalleBoleta(Boleta boleta, Producto producto, int cantidad, double precioVenta) {
        this.boleta = boleta;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioVenta = precioVenta;
    }
}
