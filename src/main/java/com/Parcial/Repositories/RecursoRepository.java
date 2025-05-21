package com.Parcial.Repositories;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Parcial.Entities.Categoria;
import com.Parcial.Entities.Recurso;


@Repository
public interface RecursoRepository extends JpaRepository<Recurso, Long> {
    Optional<Recurso> findByIsbn(String isbn);
    List<Recurso> findByTituloContainingIgnoreCase(String titulo);
    List<Recurso> findByAutorContainingIgnoreCase(String autor);
    List<Recurso> findByCategoria(Categoria categoria);
    List<Recurso> findByAnioPublicacion(int anioPublicacion);
    List<Recurso> findByDisponible(boolean disponible);
}
