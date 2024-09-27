package br.com.lagoinha.service;

import br.com.lagoinha.model.Presenca;
import br.com.lagoinha.repository.PresencaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class PresencaService {

    private final PresencaRepository presencaRepository;

    // List all Presencas
    public List<Presenca> listPresencas() {
        return presencaRepository.findAll();
    }

    // Read Presenca by cadastroId
    public List<Presenca> getPresencasByCadastroID(String cadastroId) {
        return presencaRepository.findByCadastroId(cadastroId);
    }

    // Create Presenca
    public void addPresenca(Presenca presenca) {
        String presencaId = UUID.randomUUID().toString();
        presenca.setId(presencaId);
        presencaRepository.save(presenca);
    }

    // Delete Presenca by ID
    public void deletePresenca(String id) {
        presencaRepository.delete(id);
    }
}
