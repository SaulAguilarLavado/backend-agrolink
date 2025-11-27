package com.proy.utp.backend_agrolink.persistance.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @Column(name = "precio_por_unidad", nullable = false)
    private BigDecimal precioPorUnidad;

    @Column(name = "unidad_medida", nullable = false, length = 20)
    private String unidadMedida;

    @Column(name = "stock_disponible", nullable = false)
    private Double stockDisponible;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agricultor_id", nullable = false)
    private Usuario agricultor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cosecha_id")
    private Cosecha cosecha;

    @Column(name="estado", nullable=false, length=20)
    private String estado;

    @Column(name="fecha_publicacion")
    private LocalDate fechaPublicacion;

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

    public BigDecimal getPrecioPorUnidad() {
        return precioPorUnidad;
    }

    public void setPrecioPorUnidad(BigDecimal precioPorUnidad) {
        this.precioPorUnidad = precioPorUnidad;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public Double getStockDisponible() {
        return stockDisponible;
    }

    public void setStockDisponible(Double stockDisponible) {
        this.stockDisponible = stockDisponible;
    }

    public Usuario getAgricultor() {
        return agricultor;
    }

    public void setAgricultor(Usuario agricultor) {
        this.agricultor = agricultor;
    }

    public Cosecha getCosecha() {
        return cosecha;
    }

    public void setCosecha(Cosecha cosecha) {
        this.cosecha = cosecha;
    }

    public LocalDate getFechaPublicacion() { return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDate fechaPublicacion) {this.fechaPublicacion = fechaPublicacion;
    }

    public String getEstado() { return estado;
    }

    public void setEstado(String estado) { this.estado = estado;
    }
}
