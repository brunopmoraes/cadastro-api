package br.com.lagoinha.service;

import br.com.lagoinha.model.Cadastro;
import br.com.lagoinha.model.Presenca;
import br.com.lagoinha.repository.PresencaRepository;
import br.com.lagoinha.utils.ValidadorCPF;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class PresencaService {

    private final PresencaRepository presencaRepository;
    private final CadastroService cadastroService;

    /**
     * Listar todas as presenças.
     *
     * @return lista de presenças
     */
    public List<Presenca> listPresencas() {
        return presencaRepository.findAll();
    }

    /**
     * Obter presenças por ID de cadastro.
     *
     * @param cadastroId ID do cadastro
     * @return lista de presenças associadas ao cadastro
     */
    public List<Presenca> getPresencasByCadastroID(String cadastroId) {
        return presencaRepository.findByCadastroId(cadastroId);
    }

    /**
     * Adicionar uma nova presença.
     *
     * @param presenca objeto presença a ser adicionado
     */
    public void addPresenca(Presenca presenca) {
        if (presenca == null) {
            throw new IllegalArgumentException("Presença não pode ser nula");
        }
        if (!ValidadorCPF.validarCPF(presenca.getCpf())) {
            throw new IllegalArgumentException("CPF inválido");
        }
        Cadastro cadastro = cadastroService.getCadastroByCpf(presenca.getCpf());
        if(cadastro == null) {
            throw new IllegalArgumentException("Cadastro não encontrado para o CPF informado");
        }
        // Gerar e definir um ID único para a nova presença
        presenca.setId(generateUniqueId());
        presencaRepository.save(presenca);
    }

    /**
     * Deletar uma presença pelo ID.
     *
     * @param id ID da presença a ser removida
     */
    public void deletePresenca(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID da presença não pode ser nulo ou vazio");
        }

        presencaRepository.delete(id);
    }

    // Método auxiliar para gerar um ID único
    private String generateUniqueId() {
        return UUID.randomUUID().toString();
    }
}
