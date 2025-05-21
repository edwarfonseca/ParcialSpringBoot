package com.Parcial.Controllers;


import com.Parcial.Entities.RecursoBibliografico;
import com.Parcial.Services.RecursoBibliograficoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recursos")
public class RecursoBibliograficoController {

    @Autowired
    private RecursoBibliograficoService recursoService;

    @GetMapping
    public List<RecursoBibliografico> getAll() {
        return recursoService.findAll();
    }

    @PostMapping
    public RecursoBibliografico save(@RequestBody RecursoBibliografico recurso) {
        return recursoService.save(recurso);
    }

    @GetMapping("/{id}")
    public RecursoBibliografico getById(@PathVariable Long id) {
        return recursoService.findById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        recursoService.deleteById(id);
    }
}
