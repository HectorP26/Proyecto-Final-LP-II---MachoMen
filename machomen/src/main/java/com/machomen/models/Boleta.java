package com.machomen.models;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tb_cab_boleta")
@Data
public class Boleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "num_bol")
    private Integer numBoleta;

    @Column(name = "fch_bol")
    private LocalDate fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_usua", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "boleta", cascade = CascadeType.ALL)
    private List<DetalleBoleta> lstDetalleBoleta;

    public Double getTotal() {
        if (lstDetalleBoleta == null) return 0.0;
        return lstDetalleBoleta.stream()
            .mapToDouble(d -> d.getPrecioVenta() * d.getCantidad())
            .sum();
    }
}
