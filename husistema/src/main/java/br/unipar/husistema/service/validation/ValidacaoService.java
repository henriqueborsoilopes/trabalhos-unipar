package br.unipar.husistema.service.validation;

import br.unipar.husistema.dto.AtualizarPessoaDTO;
import br.unipar.husistema.dto.CancelarConsultaDTO;
import br.unipar.husistema.dto.EnderecoDTO;
import br.unipar.husistema.dto.InserirConsultaDTO;
import br.unipar.husistema.dto.InserirMedicoDTO;
import br.unipar.husistema.dto.InserirPacienteDTO;
import br.unipar.husistema.entity.Medico;
import br.unipar.husistema.entity.Paciente;
import br.unipar.husistema.service.ConsultaService;
import br.unipar.husistema.service.MedicoService;
import br.unipar.husistema.service.PacienteService;
import br.unipar.husistema.service.exception.BancoDadosException;
import br.unipar.husistema.service.exception.Campo;
import br.unipar.husistema.service.exception.ValidacaoExcecao;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ValidacaoService {

    private static List<Campo> campos;
    private static ConsultaService consultaService;
    private static MedicoService medicoService;
    private static PacienteService pacienteService;

    public static void validarInsercaoMedico(InserirMedicoDTO dto) throws ValidacaoExcecao {
        campos = new ArrayList<>();
        
        if (dto == null) {
            campos.add(new Campo("Todos", "Devem ser preenchido!"));
            throw new ValidacaoExcecao(campos);
        }

        if (dto.getCrm().isEmpty() || dto.getCrm().isBlank()) {
            campos.add(new Campo("CRM", "Deve ser preenchido!"));
        }

        if (dto.getTipoEspecialidade() == null) {
            campos.add(new Campo("Especialidade", "Deve ser preenchido!"));
        }
        
        if (dto.getNome().isEmpty() || dto.getNome().isBlank()) {
            campos.add(new Campo("Nome", "Deve ser preenchido!"));
        }

        if (dto.getEmail().isEmpty() || dto.getEmail().isBlank()) {
            campos.add(new Campo("E-mail", "Deve ser preenchido!"));
        }
        
        if (dto.getTelefone().isEmpty() || dto.getTelefone().isBlank()) {
            campos.add(new Campo("Telefone", "Deve ser preenchido!"));
        }
        
        validarEndereco(dto.getEndereco());

        if (!campos.isEmpty()) {
            throw new ValidacaoExcecao(campos);
        }
    }

    public static void validarInsercaoPaciente(InserirPacienteDTO dto) throws ValidacaoExcecao {
        campos = new ArrayList<>();
        
        if (dto == null) {
            campos.add(new Campo("Todos", "Devem ser preenchido!"));
            throw new ValidacaoExcecao(campos);
        }

        if (dto.getNome().isEmpty() || dto.getNome().isBlank()) {
            campos.add(new Campo("Nome", "Deve ser preenchido!"));
        }

        if (dto.getEmail().isEmpty() || dto.getEmail().isBlank()) {
            campos.add(new Campo("E-mail", "Deve ser preenchido!"));
        }
        
        if (dto.getTelefone().isEmpty() || dto.getTelefone().isBlank()) {
            campos.add(new Campo("Telefone", "Deve ser preenchido!"));
        }
        
        if (dto.getCpf().isEmpty() || dto.getCpf().isBlank()) {
            campos.add(new Campo("Telefone", "Deve ser preenchido!"));
        }
        
        validarEndereco(dto.getEndereco());

        if (!campos.isEmpty()) {
            throw new ValidacaoExcecao(campos);
        }
    }
    
    public static void validarAtualizacaoPaciente(Long id, AtualizarPessoaDTO dto) throws ValidacaoExcecao {
        campos = new ArrayList<>();
        
        if (id == null) {
            campos.add(new Campo("Id", "Devem ser informado!"));
            throw new ValidacaoExcecao(campos);
        }
        
        if (dto == null) {
            campos.add(new Campo("Todos", "Devem ser preenchido!"));
            throw new ValidacaoExcecao(campos);
        }

        if (dto.getNome().isEmpty() || dto.getNome().isBlank()) {
            campos.add(new Campo("Nome", "Deve ser preenchido!"));
        }
        
        if (dto.getTelefone().isEmpty() || dto.getTelefone().isBlank()) {
            campos.add(new Campo("Telefone", "Deve ser preenchido!"));
        }
        
        validarEndereco(dto.getEndereco());

        if (!campos.isEmpty()) {
            throw new ValidacaoExcecao(campos);
        }
    }
    
    public static void validarConsulta(InserirConsultaDTO dto) throws ValidacaoExcecao, BancoDadosException {
        consultaService = new ConsultaService();
        medicoService = new MedicoService();
        pacienteService = new PacienteService();
        campos = new ArrayList<>();
        
        if (dto == null) {
            campos.add(new Campo("Todos", "Devem ser preenchido!"));
            throw new ValidacaoExcecao(campos);
        }
        
//        if (dto.getDataConsulta()) {
//            campos.add(new Campo("Data da Consulta", "Data inválida!"));
//        } else if (Duration.between(dto.getDataConsulta(), LocalDateTime.now()).toMinutes() > 30) {
//            campos.add(new Campo("Horário da Consulta", "O agendamento deve ser feito 30 minutos de antecedência!"));
//        }
//        
//        if (dto.getDataConsulta().getHour() < 7 || dto.getDataConsulta().getHour() > 18) {
//            campos.add(new Campo("Horário da Consulta", "O horário deve ser estar entre às 7 e 18 horas!"));
//        }
        
//        try {
//            Paciente entity = pacienteService.acharPorId(dto.getPacienteId());
//            if (entity == null) {
//                campos.add(new Campo("Paciente", "Paciente não contrado!"));
//            } else if (!entity.isAtivo()) {
//                campos.add(new Campo("Paciente", "Paciente inativo!"));
//            } 
//        } catch (BancoDadosException e) {
//            throw new BancoDadosException("Falha na conexão");
//        }
        
        if (consultaService.cansultarAgendamentoPaciente(dto.dataConsulta(), dto.getPacienteId())) {
            campos.add(new Campo("Paciente", "Paciente já possuí agendamento na data informada!"));
        }
        
        if (consultaService.cansultarAgendamentoMedico(dto.dataConsulta(), dto.getMedicoId())) {
            campos.add(new Campo("Médico", "Médico já possuí agendamento na data informada!"));
        }
        
        if (dto.getMedicoId()!= null) {
            try {
                Medico entity = medicoService.acharPorId(dto.getMedicoId());
                if (entity == null) {
                    campos.add(new Campo("Médico", "Médico não contrado!"));
                } else if (!entity.isAtivo()) {
                    campos.add(new Campo("Médico", "Médico inativo!"));
                }
            } catch (BancoDadosException e) {
                throw new BancoDadosException("Falha na conexão");
            }
        } else {
            Long id = medicoService.acharMedicoDisponivel();
            if (id == null) {
                campos.add(new Campo("Médico", "Não tem médico disponível!"));
            } else {
                dto.setMedicoId(id);
            }
        }
        
        if (!campos.isEmpty()) {
            throw new ValidacaoExcecao(campos);
        }
    }
    
    public static void validarCancelamentoConsulta(Long id_consulta, CancelarConsultaDTO dto) throws ValidacaoExcecao {
        campos = new ArrayList<>();
        
        if (dto == null) {
            campos.add(new Campo("Todos", "Devem ser preenchido!"));
            throw new ValidacaoExcecao(campos);
        }
        
        if (consultaService.cansultarDataConsulta(id_consulta).getHour() < 23) {
            campos.add(new Campo("Data Cancelamento", "O cancelamento deve ocorrer com antecedência mínima de 24 horas!"));
        }
        
        if (dto.getDescriCancelamento().isBlank() || dto.getDescriCancelamento().isEmpty()) {
            campos.add(new Campo("Descrição do Cancelamento", "É obrigatório o preenchimento da descrição do cancelamento!"));
        }
        
        if (!campos.isEmpty()) {
            throw new ValidacaoExcecao(campos);
        }
    }
    
    private static void validarEndereco(EnderecoDTO dto) {
        if (dto.getLogradouro().isEmpty() || dto.getLogradouro().isBlank()) {
            campos.add(new Campo("Logradouro", "Deve ser preenchido!"));
        }
        
        if (dto.getBairro().isEmpty() || dto.getBairro().isBlank()) {
            campos.add(new Campo("Bairro", "Deve ser preenchido!"));
        }
        
        if (dto.getCidade().isEmpty() || dto.getCidade().isBlank()) {
            campos.add(new Campo("Cidade", "Deve ser preenchido!"));
        }
        
        if (dto.getUf().isEmpty() || dto.getUf().isBlank()) {
            campos.add(new Campo("UF", "Deve ser preenchido!"));
        }
        
        if (dto.getCep().isEmpty() || dto.getCep().isBlank()) {
            campos.add(new Campo("CEP", "Deve ser preenchido!"));
        }
    }
}
