package br.com.lagoinha.service;

import br.com.lagoinha.model.Presenca;
import br.com.lagoinha.repository.PresencaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class PresencaService {

    private final PresencaRepository presencaRepository;

    // List all Presencas
    public List<Presenca> listPresencas() {
        return presencaRepository.findAll();
    }

    // Read Presenca by CPF
    public List<Presenca> getPresencasByCpf(String cpf) {
        return presencaRepository.findByCpf(cpf);
    }

    // Create Presenca
    public void addPresenca(Presenca presenca) {
        presencaRepository.save(presenca);
    }

    // Delete Presenca by ID
    public void deletePresenca(String id) {
        presencaRepository.delete(id);
    }
}
