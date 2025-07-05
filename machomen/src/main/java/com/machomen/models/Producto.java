package com.machomen.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import java.math.BigDecimal;

@Entity
@Table(name = "tb_productos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
public class Producto {

    @Id
    @Column(name = "id_prod", length = 5, nullable = false)
    @Pattern(regexp = "P[0-9]{4}", message = "El código debe tener el formato P0001")
    private String idProd;

    @Column(name = "des_prod", length = 45, nullable = false)
    @NotBlank(message = "La descripción no puede estar vacía")
    private String desProd;

    @Column(name = "stk_prod", nullable = false)
    @NotNull(message = "El stock es obligatorio")
    @Positive(message = "El stock debe ser un número positivo")
    private Integer stkProd;

    @Column(name = "pre_prod", nullable = false, precision = 8, scale = 2)
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor que cero")
    private BigDecimal preProd;

    @ManyToOne
    @JoinColumn(name = "idcategoria", nullable = false)
    @NotNull(message = "Debe seleccionar una categoría")
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "est_prod", nullable = false)
    @NotNull(message = "Debe seleccionar un estado")
    private Estado estado;


    

    @Transient
    public String getImagen() {
        return getIdProd() + ".jpg";
    }
}
