package br.unipar.husistema.service.imple;

import br.unipar.husistema.dto.InserirPacienteDTO;
import br.unipar.husistema.dto.ListPacienteDTO;
import br.unipar.husistema.entity.Endereco;
import br.unipar.husistema.entity.Paciente;
import br.unipar.husistema.factory.ConnectionFactory;
import br.unipar.husistema.mapper.EnderecoMapper;
import br.unipar.husistema.mapper.PacienteMapper;
import br.unipar.husistema.service.exception.BancoDadosExcecao;
import br.unipar.husistema.service.exception.ValidacaoExcecao;
import br.unipar.husistema.service.validation.PacienteValidacao;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import br.unipar.husistema.service.IPacienteService;
import br.unipar.husistema.factory.IRepositoryFactory;

public class PacienteServiceImple implements IPacienteService {
    
    private final IRepositoryFactory repository;
    
    public PacienteServiceImple(IRepositoryFactory repository) {
        this.repository = repository;
    }
    
    @Override
    public Paciente inserir(InserirPacienteDTO dto) throws BancoDadosExcecao, ValidacaoExcecao {
        ConnectionFactory.abrirConexao();
        ConnectionFactory.manterConexaoAberta(true);
        ConnectionFactory.autoCommit(false);
        PacienteValidacao.validarInsercaoPaciente(dto);
        Paciente paciente = PacienteMapper.getEntity(dto);
        Endereco endereco = EnderecoMapper.getEntity(dto.getEndereco());
        try {
            paciente.setIdEndereco(repository.getEnderecoRepository().inserir(endereco).getId());
            paciente.setId(repository.getPessoaRepository().inserir(paciente).getId());
            repository.getPacienteRepository().inserir(paciente);
            ConnectionFactory.commit();
        } catch (SQLException ex) {
            Logger.getLogger(ConsultaServiceImple.class.getName()).log(Level.SEVERE, null, ex);
            throw new BancoDadosExcecao("Desculpe, ocorreu um erro ao processar sua solicitação. Por favor, tente novamente mais tarde.");
        } finally {
            ConnectionFactory.fecharConexao();
        }
        return paciente;
    }
    
    @Override
    public Paciente acharPorId(Long id) throws BancoDadosExcecao {
        ConnectionFactory.abrirConexao();
        try {
            return repository.getPacienteRepository().acharPorId(id);
        } catch (SQLException ex) {
            Logger.getLogger(ConsultaServiceImple.class.getName()).log(Level.SEVERE, null, ex);
            throw new BancoDadosExcecao("Desculpe, ocorreu um erro ao processar sua solicitação. Por favor, tente novamente mais tarde.");
        } finally {
            ConnectionFactory.fecharConexao();
        }
    }
    
    @Override
    public List<ListPacienteDTO> acharTodos() throws BancoDadosExcecao {
        ConnectionFactory.abrirConexao();
        try {
            return PacienteMapper.getLitDTO(repository.getPacienteRepository().acharTodos());
        } catch (SQLException ex) {
            Logger.getLogger(ConsultaServiceImple.class.getName()).log(Level.SEVERE, null, ex);
            throw new BancoDadosExcecao("Desculpe, ocorreu um erro ao processar sua solicitação. Por favor, tente novamente mais tarde.");
        } finally {
            ConnectionFactory.fecharConexao();
        }
    }
}