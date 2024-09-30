package br.com.lagoinha.service;

import br.com.lagoinha.exception.CadastroNotFoundException;
import br.com.lagoinha.model.Cadastro;
import br.com.lagoinha.model.Presenca;
import br.com.lagoinha.repository.CadastroRepository;
import br.com.lagoinha.repository.PresencaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class CadastroService {
    private final CadastroRepository cadastroRepository;
    private final PresencaRepository presencaRepository;

    public void create(Cadastro cadastro) {
        cadastroRepository.save(cadastro);
    }

    public void update(String id, Cadastro cadastro) {
        Cadastro existingCadastro = findCadastroById(id);
        updateCadastroFields(existingCadastro, cadastro);
        cadastroRepository.save(existingCadastro);
    }

    private Cadastro findCadastroById(String id) {
        return cadastroRepository.findById(id)
                .orElseThrow(() -> new CadastroNotFoundException("Cadastro não encontrado para o ID: " + id));
    }

    private void updateCadastroFields(Cadastro existingCadastro, Cadastro updatedCadastro) {
        if (updatedCadastro.getNomeCompleto() != null) {
            existingCadastro.setNomeCompleto(updatedCadastro.getNomeCompleto());
        }
        if (updatedCadastro.getCelular() != null) {
            existingCadastro.setCelular(updatedCadastro.getCelular());
        }
        if (updatedCadastro.getEmail() != null) {
            existingCadastro.setEmail(updatedCadastro.getEmail());
        }
        if (updatedCadastro.getSexo() != null) {
            existingCadastro.setSexo(updatedCadastro.getSexo());
        }
        existingCadastro.setCertificado(updatedCadastro.isCertificado()); // Sem verificação, só atualiza
        if (updatedCadastro.getCpf() != null) {
            existingCadastro.setCpf(updatedCadastro.getCpf());
        }
    }

    public Cadastro getCadastroById(String id) {
        Cadastro cadastro = findCadastroById(id);
        List<Presenca> presencas = presencaRepository.findByCadastroId(id);
        cadastro.setPresencas(presencas);
        cadastro.setTotalPresencas(presencas.size());
        return cadastro;
    }

    public void deleteCadastro(String id) {
        Cadastro cadastro = findCadastroById(id);
        cadastroRepository.delete(cadastro);
    }

    public List<Cadastro> listCadastros() {
        List<Cadastro> cadastros = cadastroRepository.findAll();
        return cadastros.stream()
                .map(this::populateCadastroWithPresencas)
                .collect(Collectors.toList());
    }

    private Cadastro populateCadastroWithPresencas(Cadastro cadastro) {
        List<Presenca> presencas = presencaRepository.findByCpf(cadastro.getCpf());
        cadastro.setPresencas(presencas);
        cadastro.setTotalPresencas(presencas.size());
        return cadastro;
    }

    public List<Cadastro> completosSemCertificado() {
        return cadastroRepository.findSemCertificado().stream()
                .filter(cadastro -> checkTotalPresenca(cadastro.getId()) == 8)
                .collect(Collectors.toList());
    }

    private long checkTotalPresenca(String cadastroId) {
        return presencaRepository.countByCadastroId(cadastroId);
    }
}
