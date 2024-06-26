package br.unipar.husistema.repository.imple;

import br.unipar.husistema.entity.Medico;
import br.unipar.husistema.entity.enums.EspecialidadeEnum;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import br.unipar.husistema.repository.IMedicoRepository;
import br.unipar.husistema.service.connection.ConexaoBD;
import br.unipar.husistema.service.exception.BancoDadosExcecao;

public class MedicoRepositoryImple implements IMedicoRepository {
    
    private static final String TABELA = "medico";
    private static final String[] COLUNAS = {"id_pessoa", "crm", "tipo_especialidade"};
    private static final String TABELA_CONSULTA = "consulta";
    private static final String[] COLUNAS_CONSULTA = {"id_medico", "data_consulta", "cancelado"};
    private static final String TABELA_PESSOA = "pessoa";
    private static final String[] COLUNAS_PESSOA = {"id", "nome", "email", "telefone", "ativo", "id_endereco"};
      
    @Override
    public void inserir(Medico medico) {
        String query = ""
            + "INSERT INTO " + TABELA + " (" + COLUNAS[0] + ", " + COLUNAS[1] + ", " + COLUNAS[2] + ") "
            + "VALUES (?, ?, ?);";
         
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(query)) {
            ps.setLong(1, medico.getId());
            ps.setString(2, medico.getCrm());
            ps.setInt(3, medico.getTipoEspecialidade().getCodigo());
            ps.execute();
        } catch (SQLException e) {
            throw new BancoDadosExcecao("Desculpe, ocorreu um erro ao processar sua solicitação. Por favor, tente novamente mais tarde.");
        }
    }
    
    @Override
    public Long acharMedicoDisponivel(LocalDateTime data) {
        String query = ""
            + "SELECT m.* "
            + "FROM " + TABELA + " m "
            + "LEFT JOIN " + TABELA_CONSULTA + " c ON c." + COLUNAS_CONSULTA[0] + " = m." + COLUNAS[0] + " "
            + "WHERE c." + COLUNAS_CONSULTA[0] + " IS NULL "
            + "OR (NOT(? BETWEEN c." + COLUNAS_CONSULTA[1] + " AND c." + COLUNAS_CONSULTA[1] + " + INTERVAL '1 hour' " 
            + "OR ? BETWEEN c." + COLUNAS_CONSULTA[1] + " AND c." + COLUNAS_CONSULTA[1] + " + INTERVAL '1 hour') " 
            + "AND c." + COLUNAS_CONSULTA[2] + " = false) " 
            + "ORDER BY c." + COLUNAS_CONSULTA[0] + " DESC " 
            + "LIMIT 1;";
        
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(query)) {
            ps.setTimestamp(1, Timestamp.valueOf(data));
            ps.setTimestamp(2, Timestamp.valueOf(data.plusHours(1)));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(COLUNAS[0]);
                }
                return null;
            } catch (SQLException e) {
                throw new BancoDadosExcecao("Desculpe, ocorreu um erro ao processar sua solicitação. Por favor, tente novamente mais tarde.");
            }
        } catch (SQLException e) {
            throw new BancoDadosExcecao("Desculpe, ocorreu um erro ao processar sua solicitação. Por favor, tente novamente mais tarde.");
        }
    }
    
    @Override
    public Medico acharPorId(Long id) {
        Medico medico = null;        
        String query = ""
            + "SELECT * "
            + "FROM " + TABELA + " m "
            + "INNER JOIN " + TABELA_PESSOA + " pe ON pe." + COLUNAS_PESSOA[0] + " = m." + COLUNAS[0] + " "
            + "WHERE m." + COLUNAS[0] + " = ?;";
        
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(query)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {                    
                    medico = new Medico(
                            rs.getLong(COLUNAS_PESSOA[0]), 
                            rs.getString(COLUNAS_PESSOA[1]), 
                            rs.getString(COLUNAS_PESSOA[2]), 
                            rs.getString(COLUNAS_PESSOA[3]), 
                            rs.getBoolean(COLUNAS_PESSOA[4]), 
                            null, 
                            rs.getString(COLUNAS[1]), 
                            EspecialidadeEnum.paraEnum(rs.getInt(COLUNAS[2])));
                    
                }
                return medico;
            } catch (SQLException e) {
                throw new BancoDadosExcecao("Desculpe, ocorreu um erro ao processar sua solicitação. Por favor, tente novamente mais tarde.");
            }
        } catch (SQLException e) {
            throw new BancoDadosExcecao("Desculpe, ocorreu um erro ao processar sua solicitação. Por favor, tente novamente mais tarde.");
        }
    }
       
    @Override
    public List<Medico> acharTodos() {
        List<Medico> medicos = new ArrayList<>();
        
        String query = ""
            + "SELECT * "
            + "FROM " + TABELA + " m "
            + "INNER JOIN " + TABELA_PESSOA + " pe ON pe." + COLUNAS_PESSOA[0] + " = m." + COLUNAS[0] + " "
            + "ORDER BY pe." + COLUNAS_PESSOA[1] + " ASC;";
        
        try (PreparedStatement ps = ConexaoBD.getConexao().prepareStatement(query)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {                    
                    Medico medico = new Medico(
                            rs.getLong(COLUNAS_PESSOA[0]), 
                            rs.getString(COLUNAS_PESSOA[1]), 
                            rs.getString(COLUNAS_PESSOA[2]), 
                            rs.getString(COLUNAS_PESSOA[3]), 
                            rs.getBoolean(COLUNAS_PESSOA[4]), 
                            null, 
                            rs.getString(COLUNAS[1]), 
                            EspecialidadeEnum.paraEnum(rs.getInt(COLUNAS[2])));
                    
                    medicos.add(medico);
                }
                return medicos;
            } catch (SQLException e) {
                throw new BancoDadosExcecao("Desculpe, ocorreu um erro ao processar sua solicitação. Por favor, tente novamente mais tarde.");
            }
        } catch (SQLException e) {
            throw new BancoDadosExcecao("Desculpe, ocorreu um erro ao processar sua solicitação. Por favor, tente novamente mais tarde.");
        }
    }
}
