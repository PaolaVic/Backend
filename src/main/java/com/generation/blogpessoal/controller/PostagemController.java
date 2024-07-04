package com.generation.blogpessoal.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.generation.blogpessoal.model.Postagem;
import com.generation.blogpessoal.repository.PostagemRepository;

import jakarta.validation.Valid;
@RestController
@RequestMapping("/postagens")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PostagemController {

    @Autowired
    private PostagemRepository postagemRepository;

    // 1. Método para obter todas as postagens
    @GetMapping
    public ResponseEntity<List<Postagem>> getAll() {
        return ResponseEntity.ok(postagemRepository.findAll());
    }

    // 2. Método para obter uma postagem por ID
    @GetMapping("/{id}")
    public ResponseEntity<Postagem> getById(@PathVariable Long id) {
        Optional<Postagem> postagem = postagemRepository.findById(id);
        return postagem.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    // 3. Método para buscar postagens por título (ignorando maiúsculas/minúsculas)
    @GetMapping("/titulo/{titulo}")
    public ResponseEntity<List<Postagem>> getByTitulo(@PathVariable String titulo) {
        return ResponseEntity.ok(postagemRepository.findAllByTituloContainingIgnoreCase(titulo));
    }

    // 4. Método para criar uma nova postagem
    @PostMapping
    public ResponseEntity<Postagem> post(@Valid @RequestBody Postagem postagem) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postagemRepository.save(postagem));
    }

    // 5. Método para atualizar uma postagem existente
    @PutMapping("/{id}")
    public ResponseEntity<Postagem> put(@PathVariable Long id, @Valid @RequestBody Postagem postagemAtualizada) {
        Optional<Postagem> postagemExistente = postagemRepository.findById(id);
        if (!postagemExistente.isPresent()) {
            return ResponseEntity.notFound().build(); // Retorna not found se a postagem não existir
        }

        // Atualiza os dados da postagem existente com os dados da postagem atualizada
        Postagem postagem = postagemExistente.get();
        postagem.setTitulo(postagemAtualizada.getTitulo());
        postagem.setTexto(postagemAtualizada.getTexto());

        // Salva a postagem atualizada
        Postagem postagemSalva = postagemRepository.save(postagem);
        return ResponseEntity.ok(postagemSalva);
    }

    // 6. Método para deletar uma postagem pelo ID
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        Optional<Postagem> postagem = postagemRepository.findById(id);
        postagem.ifPresentOrElse(postagemRepository::delete, () -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Postagem não encontrada com o ID: " + id);
        });
    }
}