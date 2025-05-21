package com.Parcial.Services;


import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.Parcial.Entities.Categoria;
import com.Parcial.Repositories.CategoriaRepository;


@Service
public class CategoriaService {
    private HashMap<String, Object> datos;
    private final CategoriaRepository categoriaRepository;

    @Autowired
    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<Categoria> getCategorias() {
        return this.categoriaRepository.findAll();
    }

    public ResponseEntity<Object> getCategoriaById(Long id) {
        datos = new HashMap<>();
        Optional<Categoria> categoriaOptional = this.categoriaRepository.findById(id);
        
        if (categoriaOptional.isPresent()) {
            return new ResponseEntity<>(categoriaOptional.get(), HttpStatus.OK);
        } else {
            datos.put("error", true);
            datos.put("message", "No existe una categoría con ese ID");
            return new ResponseEntity<>(datos, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<Object> saveCategoria(Categoria categoria) {
        datos = new HashMap<>();
        
        // Verificar si ya existe una categoría con el mismo nombre
        Optional<Categoria> categoriaExistente = categoriaRepository.findByNombre(categoria.getNombre());
        if (categoriaExistente.isPresent() && !categoriaExistente.get().getId().equals(categoria.getId())) {
            datos.put("error", true);
            datos.put("message", "Ya existe una categoría con ese nombre");
            return new ResponseEntity<>(datos, HttpStatus.CONFLICT);
        }
        
        // Guardar la categoría
        Categoria savedCategoria = categoriaRepository.save(categoria);
        datos.put("message", categoria.getId() != null ? "Categoría actualizada" : "Categoría creada");
        datos.put("data", savedCategoria);
        
        return new ResponseEntity<>(datos, HttpStatus.CREATED);
    }

    public ResponseEntity<Object> deleteCategoria(Long id) {
        datos = new HashMap<>();
        Optional<Categoria> categoriaOptional = this.categoriaRepository.findById(id);
        
        if (!categoriaOptional.isPresent()) {
            datos.put("error", true);
            datos.put("message", "No existe una categoría con ese ID");
            return new ResponseEntity<>(datos, HttpStatus.NOT_FOUND);
        }
        
        Categoria categoria = categoriaOptional.get();
        if (!categoria.getRecursos().isEmpty()) {
            datos.put("error", true);
            datos.put("message", "No se puede eliminar la categoría porque tiene recursos asociados");
            return new ResponseEntity<>(datos, HttpStatus.CONFLICT);
        }
        
        categoriaRepository.deleteById(id);
        datos.put("message", "Categoría eliminada");
        return new ResponseEntity<>(datos, HttpStatus.OK);
    }
}
