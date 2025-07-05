package com.machomen.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "tb_usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_usua")
    private Integer codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 15, message = "Máximo 15 caracteres")
    @Column(name = "nom_usua")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 25, message = "Máximo 25 caracteres")
    @Column(name = "ape_usua")
    private String apellido;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Debe ser un correo válido")
    @Column(name = "user_usua", unique = true)
    private String correo;

    @NotBlank(message = "La clave es obligatoria")
    @Size(min = 5, max = 100, message = "La clave debe tener al menos 5 caracteres")
    @Column(name = "pswd_usua")
    private String clave;

    @Past(message = "Debe ser una fecha en el pasado")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fnac_usua")
    private LocalDate fechaNacimiento;

    @ManyToOne
    @JoinColumn(name = "idtipo")
    @NotNull(message = "Seleccione un tipo")
    private Tipo tipo;

    @ManyToOne
    @JoinColumn(name = "idestado")
    @NotNull(message = "Seleccione un estado")
    private Estado estado;
    
    
    

    public String getFullUsuario() {
        return String.format("%s - %s %s", correo, nombre, apellido);
    }
}
