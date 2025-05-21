package com.Parcial.Services;


import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.Parcial.Entities.Usuario;
import com.Parcial.Repositories.UsuarioRepository;


@Service
public class UsuarioService {
    private HashMap<String, Object> datos;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> getUsuarios() {
        return this.usuarioRepository.findAll();
    }

    public ResponseEntity<Object> getUsuarioById(Long id) {
        datos = new HashMap<>();
        Optional<Usuario> usuarioOptional = this.usuarioRepository.findById(id);
        
        if (usuarioOptional.isPresent()) {
            return new ResponseEntity<>(usuarioOptional.get(), HttpStatus.OK);
        } else {
            datos.put("error", true);
            datos.put("message", "No existe un usuario con ese ID");
            return new ResponseEntity<>(datos, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<Object> saveUsuario(Usuario usuario) {
        datos = new HashMap<>();
        
        // Verificar si ya existe email
        Optional<Usuario> emailExistente = usuarioRepository.findByEmail(usuario.getEmail());
        if (emailExistente.isPresent() && !emailExistente.get().getId().equals(usuario.getId())) {
            datos.put("error", true);
            datos.put("message", "Ya existe un usuario con ese email");
            return new ResponseEntity<>(datos, HttpStatus.CONFLICT);
        }
        
        // Verificar si ya existe documento
        Optional<Usuario> documentoExistente = usuarioRepository.findByDocumento(usuario.getDocumento());
        if (documentoExistente.isPresent() && !documentoExistente.get().getId().equals(usuario.getId())) {
            datos.put("error", true);
            datos.put("message", "Ya existe un usuario con ese documento");
            return new ResponseEntity<>(datos, HttpStatus.CONFLICT);
        }
        
        // Guardar el usuario
        Usuario savedUsuario = usuarioRepository.save(usuario);
        datos.put("message", usuario.getId() != null ? "Usuario actualizado" : "Usuario creado");
        datos.put("data", savedUsuario);
        
        return new ResponseEntity<>(datos, HttpStatus.CREATED);
    }

    public ResponseEntity<Object> deleteUsuario(Long id) {
        datos = new HashMap<>();
        boolean existe = this.usuarioRepository.existsById(id);
        
        if (!existe) {
            datos.put("error", true);
            datos.put("message", "No existe un usuario con ese ID");
            return new ResponseEntity<>(datos, HttpStatus.NOT_FOUND);
        }
        
        usuarioRepository.deleteById(id);
        datos.put("message", "Usuario eliminado");
        return new ResponseEntity<>(datos, HttpStatus.OK);
    }
}
