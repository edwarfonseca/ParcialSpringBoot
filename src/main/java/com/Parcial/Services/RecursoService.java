package com.Parcial.Services;


import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.Parcial.Entities.Categoria;
import com.Parcial.Entities.Recurso;
import com.Parcial.Repositories.CategoriaRepository;
import com.Parcial.Repositories.RecursoRepository;



@Service
public class RecursoService {
    private HashMap<String, Object> datos;
    private final RecursoRepository recursoRepository;
    private final CategoriaRepository categoriaRepository;

    @Autowired
    public RecursoService(RecursoRepository recursoRepository, CategoriaRepository categoriaRepository) {
        this.recursoRepository = recursoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public List<Recurso> getRecursos() {
        return this.recursoRepository.findAll();
    }

    public ResponseEntity<Object> getRecursoById(Long id) {
        datos = new HashMap<>();
        Optional<Recurso> recursoOptional = this.recursoRepository.findById(id);
        
        if (recursoOptional.isPresent()) {
            return new ResponseEntity<>(recursoOptional.get(), HttpStatus.OK);
        } else {
            datos.put("error", true);
            datos.put("message", "No existe un recurso con ese ID");
            return new ResponseEntity<>(datos, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<Object> saveRecurso(Recurso recurso) {
        datos = new HashMap<>();
        
        if (recurso.getIsbn() != null && !recurso.getIsbn().isEmpty()) {
            Optional<Recurso> recursoExistente = recursoRepository.findByIsbn(recurso.getIsbn());
            if (recursoExistente.isPresent() && !recursoExistente.get().getId().equals(recurso.getId())) {
                datos.put("error", true);
                datos.put("message", "Ya existe un recurso con ese ISBN");
                return new ResponseEntity<>(datos, HttpStatus.CONFLICT);
            }
        }
        
        if (recurso.getCategoria() != null && recurso.getCategoria().getId() != null) {
            Optional<Categoria> categoriaOptional = categoriaRepository.findById(recurso.getCategoria().getId());
            if (!categoriaOptional.isPresent()) {
                datos.put("error", true);
                datos.put("message", "La categoría especificada no existe");
                return new ResponseEntity<>(datos, HttpStatus.BAD_REQUEST);
            }
            recurso.setCategoria(categoriaOptional.get());
        }
        
        Recurso savedRecurso = recursoRepository.save(recurso);
        datos.put("message", recurso.getId() != null ? "Recurso actualizado" : "Recurso creado");
        datos.put("data", savedRecurso);
        
        return new ResponseEntity<>(datos, HttpStatus.CREATED);
    }

    public ResponseEntity<Object> deleteRecurso(Long id) {
        datos = new HashMap<>();
        Optional<Recurso> recursoOptional = this.recursoRepository.findById(id);
        
        if (!recursoOptional.isPresent()) {
            datos.put("error", true);
            datos.put("message", "No existe un recurso con ese ID");
            return new ResponseEntity<>(datos, HttpStatus.NOT_FOUND);
        }
        
        Recurso recurso = recursoOptional.get();
        if (!recurso.getPrestamos().isEmpty()) {
            datos.put("error", true);
            datos.put("message", "No se puede eliminar el recurso porque tiene préstamos asociados");
            return new ResponseEntity<>(datos, HttpStatus.CONFLICT);
        }
        
        recursoRepository.deleteById(id);
        datos.put("message", "Recurso eliminado");
        return new ResponseEntity<>(datos, HttpStatus.OK);
    }
    
    
    public List<Recurso> buscarPorTitulo(String titulo) {
        return recursoRepository.findByTituloContainingIgnoreCase(titulo);
    }
    
    public List<Recurso> buscarPorAutor(String autor) {
        return recursoRepository.findByAutorContainingIgnoreCase(autor);
    }
    
    public List<Recurso> buscarPorCategoria(Long categoriaId) {
        Optional<Categoria> categoriaOptional = categoriaRepository.findById(categoriaId);
        if (categoriaOptional.isPresent()) {
            return recursoRepository.findByCategoria(categoriaOptional.get());
        }
        return List.of();
    }
    
    public List<Recurso> buscarPorAnio(int anio) {
        return recursoRepository.findByAnioPublicacion(anio);
    }
    
    public List<Recurso> buscarDisponibles() {
        return recursoRepository.findByDisponible(true);
    }
}
