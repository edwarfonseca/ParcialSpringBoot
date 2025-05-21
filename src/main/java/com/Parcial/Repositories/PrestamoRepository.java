package com.Parcial.Repositories;



import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.Parcial.Entities.Prestamo;
import com.Parcial.Entities.Recurso;
import com.Parcial.Entities.Usuario;


@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {
    List<Prestamo> findByUsuario(Usuario usuario);
    List<Prestamo> findByRecurso(Recurso recurso);
    
    // Préstamos activos (sin fecha de devolución)
    List<Prestamo> findByFechaDevolucionRealIsNull();
    
    // Préstamos retrasados (sin devolver y pasada la fecha estimada)
    @Query("SELECT p FROM Prestamo p WHERE p.fechaDevolucionReal IS NULL AND p.fechaDevolucionEstimada < ?1")
    List<Prestamo> findPrestamosRetrasados(LocalDate fechaActual);
    
    // Préstamos por usuario con retraso
    @Query("SELECT p FROM Prestamo p WHERE p.usuario = ?1 AND p.fechaDevolucionReal IS NULL AND p.fechaDevolucionEstimada < ?2")
    List<Prestamo> findPrestamosRetrasadosByUsuario(Usuario usuario, LocalDate fechaActual);
    
    // Préstamos realizados en un rango de fechas
    List<Prestamo> findByFechaPrestamoGreaterThanEqualAndFechaPrestamoLessThanEqual(LocalDate fechaInicio, LocalDate fechaFin);
}

