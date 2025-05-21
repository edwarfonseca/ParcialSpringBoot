package com.Parcial.Services;


import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.Parcial.Entities.Prestamo;
import com.Parcial.Entities.Recurso;
import com.Parcial.Entities.Usuario;
import com.Parcial.Repositories.PrestamoRepository;
import com.Parcial.Repositories.RecursoRepository;
import com.Parcial.Repositories.UsuarioRepository;



@Service
public class PrestamoService {
    private HashMap<String, Object> datos;
    private final PrestamoRepository prestamoRepository;
    private final UsuarioRepository usuarioRepository;
    private final RecursoRepository recursoRepository;

    @Autowired
    public PrestamoService(PrestamoRepository prestamoRepository, UsuarioRepository usuarioRepository,
            RecursoRepository recursoRepository) {
        this.prestamoRepository = prestamoRepository;
        this.usuarioRepository = usuarioRepository;
        this.recursoRepository = recursoRepository;
    }

    public List<Prestamo> getPrestamos() {
        return this.prestamoRepository.findAll();
    }

    public ResponseEntity<Object> getPrestamoById(Long id) {
        datos = new HashMap<>();
        Optional<Prestamo> prestamoOptional = this.prestamoRepository.findById(id);
        
        if (prestamoOptional.isPresent()) {
            return new ResponseEntity<>(prestamoOptional.get(), HttpStatus.OK);
        } else {
            datos.put("error", true);
            datos.put("message", "No existe un préstamo con ese ID");
            return new ResponseEntity<>(datos, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<Object> crearPrestamo(Prestamo prestamo) {
        datos = new HashMap<>();
        
        if (prestamo.getUsuario() == null || prestamo.getUsuario().getId() == null) {
            datos.put("error", true);
            datos.put("message", "Debe especificar un usuario para el préstamo");
            return new ResponseEntity<>(datos, HttpStatus.BAD_REQUEST);
        }
        
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(prestamo.getUsuario().getId());
        if (!usuarioOptional.isPresent()) {
            datos.put("error", true);
            datos.put("message", "El usuario especificado no existe");
            return new ResponseEntity<>(datos, HttpStatus.BAD_REQUEST);
        }
        
        if (prestamo.getRecurso() == null || prestamo.getRecurso().getId() == null) {
            datos.put("error", true);
            datos.put("message", "Debe especificar un recurso para el préstamo");
            return new ResponseEntity<>(datos, HttpStatus.BAD_REQUEST);
        }
        
        Optional<Recurso> recursoOptional = recursoRepository.findById(prestamo.getRecurso().getId());
        if (!recursoOptional.isPresent()) {
            datos.put("error", true);
            datos.put("message", "El recurso especificado no existe");
            return new ResponseEntity<>(datos, HttpStatus.BAD_REQUEST);
        }
        
        Recurso recurso = recursoOptional.get();
        
        if (!recurso.isDisponible()) {
            datos.put("error", true);
            datos.put("message", "El recurso no está disponible para préstamo");
            return new ResponseEntity<>(datos, HttpStatus.CONFLICT);
        }

        prestamo.setUsuario(usuarioOptional.get());
        prestamo.setRecurso(recurso);
        
        if (prestamo.getFechaPrestamo() == null) {
            prestamo.setFechaPrestamo(LocalDate.now());
        }
        
        if (prestamo.getFechaDevolucionEstimada() == null) {
            prestamo.setFechaDevolucionEstimada(prestamo.getFechaPrestamo().plusDays(15));
        }
        
        recurso.setDisponible(false);
        recursoRepository.save(recurso);
        
        Prestamo savedPrestamo = prestamoRepository.save(prestamo);
        datos.put("message", "Préstamo registrado correctamente");
        datos.put("data", savedPrestamo);
        
        return new ResponseEntity<>(datos, HttpStatus.CREATED);
    }

    public ResponseEntity<Object> devolverRecurso(Long prestamoId) {
        datos = new HashMap<>();
        Optional<Prestamo> prestamoOptional = prestamoRepository.findById(prestamoId);
        
        if (!prestamoOptional.isPresent()) {
            datos.put("error", true);
            datos.put("message", "No existe un préstamo con ese ID");
            return new ResponseEntity<>(datos, HttpStatus.NOT_FOUND);
        }
        
        Prestamo prestamo = prestamoOptional.get();
        
        if (prestamo.getFechaDevolucionReal() != null) {
            datos.put("error", true);
            datos.put("message", "Este préstamo ya fue devuelto");
            return new ResponseEntity<>(datos, HttpStatus.CONFLICT);
        }
        
        prestamo.setFechaDevolucionReal(LocalDate.now());
        
        Recurso recurso = prestamo.getRecurso();
        recurso.setDisponible(true);
        recursoRepository.save(recurso);
        
        prestamoRepository.save(prestamo);
        
        datos.put("message", "Devolución registrada correctamente");
        datos.put("retrasado", prestamo.isRetrasado());
        datos.put("diasRetraso", prestamo.getDiasRetraso());
        
        return new ResponseEntity<>(datos, HttpStatus.OK);
    }
    
    
    public List<Prestamo> getPrestamosActivos() {
        return prestamoRepository.findByFechaDevolucionRealIsNull();
    }
    
    public List<Prestamo> getPrestamosRetrasados() {
        return prestamoRepository.findPrestamosRetrasados(LocalDate.now());
    }
    
    public List<Prestamo> getPrestamosPorUsuario(Long usuarioId) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(usuarioId);
        if (usuarioOptional.isPresent()) {
            return prestamoRepository.findByUsuario(usuarioOptional.get());
        }
        return List.of();
    }
    
    public List<Prestamo> getPrestamosRetrasadosPorUsuario(Long usuarioId) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(usuarioId);
        if (usuarioOptional.isPresent()) {
            return prestamoRepository.findPrestamosRetrasadosByUsuario(usuarioOptional.get(), LocalDate.now());
        }
        return List.of();
    }
    
    public List<Prestamo> getPrestamosPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return prestamoRepository.findByFechaPrestamoGreaterThanEqualAndFechaPrestamoLessThanEqual(fechaInicio, fechaFin);
    }
}
