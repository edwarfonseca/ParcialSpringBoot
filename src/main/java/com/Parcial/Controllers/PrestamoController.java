package com.Parcial.Controllers;

import com.Parcial.Entities.Prestamo;
import com.Parcial.Services.PrestamoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prestamos")
public class PrestamoController {

    @Autowired
    private PrestamoService prestamoService;

    @GetMapping
    public List<Prestamo> getAll() {
        return prestamoService.findAll();
    }

    @PostMapping
    public Prestamo save(@RequestBody Prestamo prestamo) {
        return prestamoService.save(prestamo);
    }

    @GetMapping("/{id}")
    public Prestamo getById(@PathVariable Long id) {
        return prestamoService.findById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        prestamoService.deleteById(id);
    }

    @GetMapping("/retardos")
    public List<Prestamo> getPrestamosAtrasados() {
        return prestamoService.obtenerPrestamosAtrasados();
    }
}
