package com.Parcial.Entities;


import java.time.LocalDate;
import java.time.Period;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "prestamos")
public class Prestamo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucionEstimada;
    private LocalDate fechaDevolucionReal;
    
    @Transient
    private boolean retrasado;
    
    @Transient
    private int diasRetraso;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @JsonBackReference(value = "usuario-prestamo")
    private Usuario usuario;
    
    @ManyToOne
    @JoinColumn(name = "recurso_id")
    @JsonBackReference(value = "recurso-prestamo")
    private Recurso recurso;

    public Prestamo() {
    }

    public Prestamo(Long id, LocalDate fechaPrestamo, LocalDate fechaDevolucionEstimada, LocalDate fechaDevolucionReal,
            Usuario usuario, Recurso recurso) {
        this.id = id;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucionEstimada = fechaDevolucionEstimada;
        this.fechaDevolucionReal = fechaDevolucionReal;
        this.usuario = usuario;
        this.recurso = recurso;
    }

    public Prestamo(LocalDate fechaPrestamo, LocalDate fechaDevolucionEstimada, Usuario usuario, Recurso recurso) {
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucionEstimada = fechaDevolucionEstimada;
        this.usuario = usuario;
        this.recurso = recurso;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(LocalDate fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public LocalDate getFechaDevolucionEstimada() {
        return fechaDevolucionEstimada;
    }

    public void setFechaDevolucionEstimada(LocalDate fechaDevolucionEstimada) {
        this.fechaDevolucionEstimada = fechaDevolucionEstimada;
    }

    public LocalDate getFechaDevolucionReal() {
        return fechaDevolucionReal;
    }

    public void setFechaDevolucionReal(LocalDate fechaDevolucionReal) {
        this.fechaDevolucionReal = fechaDevolucionReal;
    }

    public boolean isRetrasado() {
        if (fechaDevolucionReal == null) {
            // Si aún no se ha devuelto, verificamos si ya pasó la fecha estimada
            return LocalDate.now().isAfter(fechaDevolucionEstimada);
        } else {
            // Si ya se devolvió, verificamos si se devolvió después de la fecha estimada
            return fechaDevolucionReal.isAfter(fechaDevolucionEstimada);
        }
    }

    public int getDiasRetraso() {
        if (!isRetrasado()) {
            return 0;
        }
        
        LocalDate fechaComparacion = fechaDevolucionReal != null ? fechaDevolucionReal : LocalDate.now();
        return Period.between(fechaDevolucionEstimada, fechaComparacion).getDays();
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Recurso getRecurso() {
        return recurso;
    }

    public void setRecurso(Recurso recurso) {
        this.recurso = recurso;
    }
}
