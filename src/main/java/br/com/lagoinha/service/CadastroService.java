package br.com.lagoinha.service;

import br.com.lagoinha.exception.CadastroNotFoundException;
import br.com.lagoinha.model.Cadastro;
import br.com.lagoinha.model.Presenca;
import br.com.lagoinha.repository.CadastroRepository;
import br.com.lagoinha.repository.PresencaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
public class CadastroService {
    private final CadastroRepository cadastroRepository;
    private final PresencaRepository presencaRepository;

    // Injeção de dependências via construtor (Princípio da Inversão de Dependência - SOLID)
    public CadastroService(CadastroRepository cadastroRepository, PresencaRepository presencaRepository) {
        this.cadastroRepository = cadastroRepository;
        this.presencaRepository = presencaRepository;
    }

    /**
     * Cria um cadastro.
     *
     * @param cadastro objeto de cadastro a ser salvo
     */
    public void create(Cadastro cadastro) {
        cadastroRepository.save(cadastro);
    }

    public void update(String cpf, Cadastro cadastro) {
        Cadastro _cadastro = cadastroRepository.findByCpf(cpf);
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

        // Salva as alterações
        cadastroRepository.save(_cadastro);
    }

    /**
     * Busca um cadastro por CPF.
     *
     * @param cpf CPF a ser buscado
     * @return Cadastro encontrado com total de presenças
     * @throws CadastroNotFoundException se o cadastro não for encontrado
     */
    public Cadastro getCadastro(String cpf) {
        Cadastro cadastro = cadastroRepository.findByCpf(cpf);
        if (Objects.isNull(cadastro)) {
            throw new CadastroNotFoundException("Cadastro não encontrado para o CPF: " + cpf);
        }

        List<Presenca> presencas = presencaRepository.findByCpf(cpf);
        cadastro.setPresencas(presencas);
        // Conta o total de presenças e atualiza no cadastro
        long totalPresencas = presencas.size();
        cadastro.setTotalPresencas(totalPresencas);
        return cadastro;
    }

    /**
     * Exclui um cadastro por CPF.
     *
     * @param cpf CPF do cadastro a ser excluído
     */
    public void deleteCadastro(String cpf) {
        Cadastro cadastro = cadastroRepository.findByCpf(cpf);
        if (Objects.isNull(cadastro)) {
            throw new CadastroNotFoundException("Cadastro não encontrado para o CPF: " + cpf);
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
                    List<Presenca> presencas = presencaRepository.findByCpf(cadastro.getCpf());
                    cadastro.setPresencas(presencas);
                    cadastro.setTotalPresencas(presencas.size());
                })
                .collect(Collectors.toList());
    }

    /**
     * Confirma o recebimento do certificado para um cadastro.
     *
     * @param cpf CPF do cadastro a ser atualizado
     * @return Cadastro atualizado
     * @throws CadastroNotFoundException se o cadastro não for encontrado
     */
    public Cadastro confirmarCertificado(String cpf) {
        Cadastro cadastro = cadastroRepository.findByCpf(cpf);
        if (Objects.isNull(cadastro)) {
            throw new CadastroNotFoundException("Cadastro não encontrado para o CPF: " + cpf);
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
                .peek(cadastro -> cadastro.setTotalPresencas(presencaRepository.countByCpf(cadastro.getCpf())))
                .filter(cadastro -> cadastro.getTotalPresencas() == 8)
                .collect(Collectors.toList());
    }
}
