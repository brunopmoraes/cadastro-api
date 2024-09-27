package br.com.lagoinha.service;

import br.com.lagoinha.exception.CadastroNotFoundException;
import br.com.lagoinha.model.Cadastro;
import br.com.lagoinha.model.Presenca;
import br.com.lagoinha.repository.CadastroRepository;
import br.com.lagoinha.repository.PresencaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class CadastroService {
    private final CadastroRepository cadastroRepository;
    private final PresencaRepository presencaRepository;

    /**
     * Cria um cadastro.
     *
     * @param cadastro objeto de cadastro a ser salvo
     */
    public void create(Cadastro cadastro) {
        cadastroRepository.save(cadastro);
    }

    public void update(String id, Cadastro cadastro) {
        Cadastro _cadastro = cadastroRepository.findById(id);
        if (Objects.isNull(_cadastro)) {
            throw new CadastroNotFoundException("Cadastro não encontrado");
        }

        // Atualiza apenas os campos que foram alterados
        if (cadastro.getNomeCompleto() != null && !cadastro.getNomeCompleto().equals(_cadastro.getNomeCompleto())) {
            _cadastro.setNomeCompleto(cadastro.getNomeCompleto());
        }
        if (cadastro.getCelular() != null && !cadastro.getCelular().equals(_cadastro.getCelular())) {
            _cadastro.setCelular(cadastro.getCelular());
        }
        if (cadastro.getEmail() != null && !cadastro.getEmail().equals(_cadastro.getEmail())) {
            _cadastro.setEmail(cadastro.getEmail());
        }
        if (cadastro.getSexo() != null && !cadastro.getSexo().equals(_cadastro.getSexo())) {
            _cadastro.setSexo(cadastro.getSexo());
        }
        if (cadastro.isCertificado() != _cadastro.isCertificado()) {
            _cadastro.setCertificado(cadastro.isCertificado());
        }
        if (cadastro.getCpf() != null && !cadastro.getCpf().equals(_cadastro.getCpf())) {
            _cadastro.setCpf(cadastro.getCpf());
        }

        // Salva as alterações
        cadastroRepository.save(_cadastro);
    }

    /**
     * Busca um cadastro por CPF.
     *
     * @param id Id a ser buscado
     * @return Cadastro encontrado com total de presenças
     * @throws CadastroNotFoundException se o cadastro não for encontrado
     */
    public Cadastro getCadastroById(String id) {
        Cadastro cadastro = cadastroRepository.findById(id);
        if (Objects.isNull(cadastro)) {
            throw new CadastroNotFoundException("Cadastro não encontrado para o Id: " + id);
        }

        List<Presenca> presencas = presencaRepository.findByCadastroId(id);
        cadastro.setPresencas(presencas);
        // Conta o total de presenças e atualiza no cadastro
        long totalPresencas = presencas.size();
        cadastro.setTotalPresencas(totalPresencas);
        return cadastro;
    }

    /**
     * Exclui um cadastro por CPF.
     *
     * @param id ID do cadastro a ser excluído
     */
    public void deleteCadastro(String id) {
        Cadastro cadastro = cadastroRepository.findById(id);
        if (Objects.isNull(cadastro)) {
            throw new CadastroNotFoundException("Cadastro não encontrado para o ID: " + id);
        }
        cadastroRepository.delete(cadastro);
    }

    /**
     * Lista todos os cadastros e adiciona o total de presenças para cada um.
     *
     * @return Lista de cadastros com total de presenças
     */
    public List<Cadastro> listCadastros() {
        return cadastroRepository.findAll()
                .stream()
                .peek(cadastro -> {
                    List<Presenca> presencas = presencaRepository.findByCadastroId(cadastro.getId());
                    cadastro.setPresencas(presencas);
                    cadastro.setTotalPresencas(presencas.size());
                })
                .collect(Collectors.toList());
    }

    /**
     * Confirma o recebimento do certificado para um cadastro.
     *
     * @param id ID do cadastro a ser atualizado
     * @return Cadastro atualizado
     * @throws CadastroNotFoundException se o cadastro não for encontrado
     */
    public Cadastro confirmarCertificado(String id) {
        Cadastro cadastro = cadastroRepository.findById(id);
        if (Objects.isNull(cadastro)) {
            throw new CadastroNotFoundException("Cadastro não encontrado para o ID: " + id);
        }

        // Atualiza o campo de certificado e salva
        cadastro.setCertificado(true);
        cadastroRepository.save(cadastro);
        return cadastro;
    }

    /**
     * Retorna cadastros que completaram 8 presenças, mas ainda não receberam certificado.
     *
     * @return Lista de cadastros sem certificado e com 8 presenças
     */
    public List<Cadastro> completosSemCertificado() {
        return cadastroRepository.findSemCertificado()
                .stream()
                .peek(cadastro -> cadastro.setTotalPresencas(presencaRepository.countByCadastroId(cadastro.getId())))
                .filter(cadastro -> cadastro.getTotalPresencas() == 8)
                .collect(Collectors.toList());
    }
}
