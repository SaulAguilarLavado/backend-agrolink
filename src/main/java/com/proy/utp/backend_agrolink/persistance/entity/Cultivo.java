package com.proy.utp.backend_agrolink.persistance.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "cultivos")
public class Cultivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    @Column(name = "fecha_siembra")
    private LocalDate fechaSiembra;

    @Column(name = "area_cultivada")
    private Double areaCultivada;

    @Column(length = 50)
    private String estado;

    @Column(name = "fecha_cosecha")
    private LocalDate fechaCosecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agricultor_id", nullable = false)
    private Usuario agricultor;

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFechaSiembra() {
        return fechaSiembra;
    }

    public void setFechaSiembra(LocalDate fechaSiembra) {
        this.fechaSiembra = fechaSiembra;
    }

    public Double getAreaCultivada() {
        return areaCultivada;
    }

    public void setAreaCultivada(Double areaCultivada) {
        this.areaCultivada = areaCultivada;
    }

    public Usuario getAgricultor() {
        return agricultor;
    }

    public void setAgricultor(Usuario agricultor) {
        this.agricultor = agricultor;
    }

    public String getEstado() {return estado;
    }

    public void setEstado(String estado) {this.estado = estado;
    }

    public LocalDate getFechaCosecha() {return fechaCosecha;
    }

    public void setFechaCosecha(LocalDate fechaCosecha) {this.fechaCosecha = fechaCosecha;
    }
}
