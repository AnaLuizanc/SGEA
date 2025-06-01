package com.mycompany.sgea;

import domain.Avaliacao;
import domain.Certificado;
import domain.Evento;
import domain.Inscricao;
import domain.Participante;
import domain.Trabalho;
import domain.enums.StatusTrabalho;
import domain.enums.TipoPerfil;
import facade.SGEAFacade;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 *
 * @author enio1
 */
public class Main {

    private static final SGEAFacade facade = new SGEAFacade();
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE; // yyyy-MM-dd

    private static Participante participanteLogado = null;

    public static void main(String[] args) {
        seedInitialData(); // Para facilitar testes

        int choice;
        do {
            exibirMenuPrincipal();
            choice = lerInteiro("Sua escolha: ");

            if (participanteLogado == null) {
                processarEscolhaMenuDeslogado(choice);
            } else {
                processarEscolhaMenuLogado(choice);
            }
        } while (choice != 0);

        System.out.println("FIM!");
        scanner.close();
    }

    private static void exibirMenuPrincipal() {
        System.out.println("\n===== SGEA - Sistema de Gerenciamento de Eventos Acadêmicos =====");
        if (participanteLogado == null) {
            System.out.println("1. Login");
            System.out.println("2. Cadastrar Novo Participante");
            System.out.println("3. Visualizar Eventos Disponíveis");
            System.out.println("4. Validar Certificado");
            System.out.println("0. Sair");
        } else {
            System.out.println("Logado como: " + participanteLogado.getNomeCompleto() + " (" + participanteLogado.getTipoPerfil() + ")");
            System.out.println("-- Menu Participante --");
            System.out.println("1. Visualizar Eventos Disponíveis");
            System.out.println("2. Inscrever-se em Evento");
            System.out.println("3. Ver Minhas Inscrições");
            System.out.println("4. Cancelar Inscrição");
            System.out.println("5. Submeter Trabalho");
            System.out.println("6. Ver Meus Trabalhos Submetidos");
            System.out.println("7. Ver Meus Certificados");

            if (participanteLogado.getTipoPerfil() == TipoPerfil.ORGANIZADOR) {
                System.out.println("-- Menu Organizador --");
                System.out.println("8. Cadastrar Novo Evento");
                System.out.println("9. Gerenciar Meus Eventos");
                System.out.println("10. Designar Avaliador para Trabalho");
                System.out.println("11. Atualizar Status de Trabalho");
                System.out.println("12. Emitir Certificados do Evento");
            }
            if (participanteLogado.getTipoPerfil() == TipoPerfil.AVALIADOR) {
                System.out.println("-- Menu Avaliador --");
                System.out.println("13. Listar Trabalhos para Avaliar");
                System.out.println("14. Registrar Avaliação de Trabalho");
            }
            System.out.println("-- Sistema --");
            System.out.println("15. Logout");
            System.out.println("0. Sair do Sistema");
        }
    }

    private static void processarEscolhaMenuDeslogado(int choice) {
        switch (choice) {
            case 1 ->
                login(); //ok
            case 2 ->
                cadastrarParticipante(); //ok
            case 3 ->
                visualizarEventosDisponiveisPublico();
            case 4 ->
                validarCertificado();
            case 0 -> {
            }
            default ->
                System.out.println("Opção inválida.");
        }

    }

    private static void processarEscolhaMenuLogado(int choice) {
        switch (choice) {
            case 1 ->
                visualizarEventosDisponiveisPublico(); //ok
            case 2 ->
                inscreverEmEvento(); //ok
            case 3 ->
                verMinhasInscricoes(); //ok
            case 4 ->
                cancelarInscricao();
            case 5 ->
                submeterTrabalho();
            case 6 ->
                verMeusTrabalhos();
            case 7 ->
                verMeusCertificados();
            case 8 -> {
                if (isPerfil(TipoPerfil.ORGANIZADOR)) {
                    cadastrarNovoEvento();
                } else {
                    opcaoInvalida();
                }
            }
            case 9 -> {
                if (isPerfil(TipoPerfil.ORGANIZADOR)) {
                    gerenciarMeusEventos();
                } else {
                    opcaoInvalida();
                }
            }
            case 10 -> {
                if (isPerfil(TipoPerfil.ORGANIZADOR)) {
                    designarAvaliador();
                } else {
                    opcaoInvalida();
                }
            }
            case 11 -> {
                if (isPerfil(TipoPerfil.ORGANIZADOR)) {
                    atualizarStatusTrabalho();
                } else {
                    opcaoInvalida();
                }
            }
            case 12 -> {
                if (isPerfil(TipoPerfil.ORGANIZADOR)) {
                    emitirCertificadosDoEvento();
                } else {
                    opcaoInvalida();
                }
            }
            case 13 -> {
                if (isPerfil(TipoPerfil.AVALIADOR)) {
                    listarTrabalhosParaAvaliar();
                } else {
                    opcaoInvalida();
                }
            }
            case 14 -> {
                if (isPerfil(TipoPerfil.AVALIADOR)) {
                    registrarAvaliacao();
                } else {
                    opcaoInvalida();
                }
            }
            case 15 ->
                validarCertificado();
            case 16 ->
                logout();
            case 17 -> {
            }
            default ->
                System.out.println("Opção inválida.");
        }

    }

    private static boolean isPerfil(TipoPerfil perfilNecessario) {
        return participanteLogado != null && participanteLogado.getTipoPerfil() == perfilNecessario;
    }

    private static void opcaoInvalida() {
        System.out.println("Opção inválida ou perfil inadequado.");
    }

    // --- Métodos de Ação ---
    private static void login() {
        System.out.println("\n--- Login ---");
        String id = lerString("ID do Participante: ");
        try {
            Optional<Participante> optP = facade.getParticipanteById(id);
            if (optP.isPresent()) {
                participanteLogado = optP.get();
                System.out.println("Login bem-sucedido!");
            } else {
                System.out.println("Participante não encontrado.");
            }
        } catch (Exception e) {
            System.err.println("Erro no login: " + e.getMessage());
        }
    }

    private static void logout() {
        System.out.println(participanteLogado.getNomeCompleto() + " deslogado.");
        participanteLogado = null;
    }

    //  UC1: O participante realiza seu cadastro no sistema
    private static void cadastrarParticipante() {
        System.out.println("\n--- Cadastro de Novo Participante ---");
        String nome = lerString("Nome completo: ");
        String email = lerString("E-mail: ");
        String instituicao = lerString("Instituição: ");
        TipoPerfil perfil = TipoPerfil.PARTICIPANTE;
        try {
            Participante p = facade.cadastrarParticipante(nome, email, instituicao, perfil);
            System.out.println("Participante cadastrado com sucesso! ID: " + p.getId());
        } catch (Exception e) {
            System.err.println("Erro ao cadastrar: " + e.getMessage());
        }
    }

    private static void visualizarEventosDisponiveisPublico() {
        System.out.println("\n--- Eventos Disponíveis ---");
        try {
            List<Evento> eventos = facade.listarEventosDisponiveisParaInscricao();
            if (eventos.isEmpty()) {
                System.out.println("Nenhum evento disponível no momento.");
            } else {
                eventos.forEach(e -> System.out.println(e));
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar eventos: " + e.getMessage());
        }
    }

    private static void validarCertificado() {
        System.out.println("\n--- Validar Certificado ---");
        String codigo = lerString("Código de validação do certificado: ");
        try {
            Optional<Certificado> optCert = facade.getCertificadoByCodigoValidacao(codigo);
            if (optCert.isPresent()) {
                Certificado cert = optCert.get();
                System.out.println("Certificado VÁLIDO:");
                System.out.println("  Tipo: " + cert.getTipo());
                System.out.println("  Emitido para: " + cert.getParticipante().getNomeCompleto());
                System.out.println("  Evento: " + cert.getEvento().getNome());
                if (cert.getTrabalho() != null) {
                    System.out.println("  Trabalho: " + cert.getTrabalho().getTitulo());
                }
                System.out.println("  Data de Emissão: " + cert.getDataEmissao().format(DATE_FORMATTER));
            } else {
                System.out.println("Certificado com código '" + codigo + "' não encontrado ou inválido.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao validar certificado: " + e.getMessage());
        }
    }

     // UC2: O participante visualiza a lista de eventos disponíveis e se inscreve em um evento de seu interesse
    private static void inscreverEmEvento() {
        System.out.println("\n--- Inscrever-se em Evento ---");
        visualizarEventosDisponiveisPublico();
        String eventoId = lerString("ID do Evento para inscrição: ");
        try {
            Inscricao insc = facade.inscreverEmEvento(participanteLogado.getId(), eventoId);
            System.out.println("Inscrição realizada com sucesso! ID da Inscrição: " + insc.getId());
        } catch (Exception e) {
            System.err.println("Erro na inscrição: " + e.getMessage());
        }
    }

    private static void verMinhasInscricoes() {
        System.out.println("\n--- Minhas Inscrições ---");
        try {
            List<Inscricao> inscricoes = facade.listarInscricoesPorParticipante(participanteLogado.getId());
            if (inscricoes.isEmpty()) {
                System.out.println("Você não possui inscrições.");
            } else {
                inscricoes.forEach(i -> System.out.println(i.getId() + " - Evento: " + i.getEvento().getNome() + " (Status: " + i.getStatus() + (i.isPresencaConfirmada() ? ", Presença Confirmada" : "") + ")"));
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar inscrições: " + e.getMessage());
        }
    }

    private static void cancelarInscricao() {
        System.out.println("\n--- Cancelar Inscrição ---");
        verMinhasInscricoes();
        String inscricaoId = lerString("ID da Inscrição a cancelar: ");
        try {
            facade.cancelarInscricao(inscricaoId);
            System.out.println("Inscrição cancelada com sucesso.");
        } catch (Exception e) {
            System.err.println("Erro ao cancelar inscrição: " + e.getMessage());
        }
    }

    private static void submeterTrabalho() {
        System.out.println("\n--- Submeter Trabalho ---");
        verMinhasInscricoes();
        String eventoId = lerString("ID do Evento para submissão: ");
        String titulo = lerString("Título do Trabalho: ");
        String arquivo = lerString("Nome do Arquivo: ");
        String autor = participanteLogado.getId();

        try {
            Trabalho t = facade.submeterTrabalho(autor, eventoId, titulo, arquivo);
            System.out.println("Trabalho '" + t.getTitulo() + "' submetido com sucesso! ID: " + t.getId());
        } catch (Exception e) {
            System.err.println("Erro ao submeter trabalho: " + e.getMessage());
        }
    }

    private static void verMeusTrabalhos() {
        System.out.println("\n--- Meus Trabalhos Submetidos ---");
        try {
            List<Trabalho> trabalhos = facade.listarTrabalhosPorAutor(participanteLogado.getId());
            if (trabalhos.isEmpty()) {
                System.out.println("Você não submeteu trabalhos.");
            } else {
                trabalhos.forEach(t -> System.out.println(t.getId() + " - Título: " + t.getTitulo() + " (Evento: " + t.getEvento().getNome() + ", Status: " + t.getStatus() + ")"));
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar trabalhos: " + e.getMessage());
        }
    }

    private static void verMeusCertificados() {
        System.out.println("\n--- Meus Certificados ---");
        try {
            List<Certificado> certificados = facade.listarCertificadosPorParticipante(participanteLogado.getId());
            if (certificados.isEmpty()) {
                System.out.println("Você não possui certificados.");
            } else {
                certificados.forEach(c -> System.out.println(
                        c.getId() + " - Tipo: " + c.getTipo()
                        + ", Evento: " + c.getEvento().getNome()
                        + (c.getTrabalho() != null ? ", Trabalho: " + c.getTrabalho().getTitulo() : "")
                        + ", Código: " + c.getCodigoValidacao()
                ));
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar certificados: " + e.getMessage());
        }
    }

    // --- Métodos do Organizador ---
    private static void cadastrarNovoEvento() {
        System.out.println("\n--- Cadastrar Novo Evento ---");
        String nome = lerString("Nome do Evento: ");
        String desc = lerString("Descrição: ");
        LocalDate dataInicio = lerData("Data de Início (yyyy-MM-dd): ");
        LocalDate dataFim = lerData("Data de Fim (yyyy-MM-dd): ");
        String local = lerString("Local/Link: ");
        int capacidade = lerInteiro("Capacidade Máxima: ");
        LocalDate subInicio = lerDataOpcional("Data de Início da Submissão (yyyy-MM-dd ou enter para pular): ");
        LocalDate subFim = null;
        if (subInicio != null) {
            subFim = lerData("Data de Fim da Submissão (yyyy-MM-dd): ");
        }

        try {
            Evento evento = facade.cadastrarEvento(nome, desc, dataInicio, dataFim, local, capacidade, participanteLogado.getId(), subInicio, subFim);
            System.out.println("Evento '" + evento.getNome() + "' cadastrado com sucesso! ID: " + evento.getId());
        } catch (Exception e) {
            System.err.println("Erro ao cadastrar evento: " + e.getMessage());
        }
    }

    private static void gerenciarMeusEventos() {
        System.out.println("\n--- Gerenciar Meus Eventos ---");
        List<Evento> meusEventos = facade.listarEventosPorOrganizador(participanteLogado.getId());
        if (meusEventos.isEmpty()) {
            System.out.println("Você não organizou nenhum evento.");
            return;
        }
        System.out.println("Seus Eventos:");
        meusEventos.forEach(e -> System.out.println(e.getId() + " - " + e.getNome()));
        String eventoId = lerString("ID do Evento para gerenciar: ");
        Optional<Evento> optEvento = meusEventos.stream().filter(e -> e.getId().equals(eventoId)).findFirst();

        if (optEvento.isEmpty()) {
            System.out.println("Evento não encontrado ou não pertence a você.");
            return;
        }
        Evento eventoSelecionado = optEvento.get();
        System.out.println("Gerenciando Evento: " + eventoSelecionado.getNome());
        System.out.println("1. Visualizar Inscritos");
        System.out.println("2. Confirmar Presença de Participante");
        System.out.println("3. Editar Detalhes do Evento");
        System.out.println("4. Definir/Alterar Período de Submissão de Trabalhos");
        System.out.println("0. Voltar");
        int escolha = lerInteiro("Sua escolha: ");

        try {
            switch (escolha) {
                case 1:
                    List<Inscricao> inscricoes = facade.visualizarInscritosEvento(eventoId);
                    System.out.println("Inscritos no evento '" + eventoSelecionado.getNome() + "':");
                    if (inscricoes.isEmpty()) {
                        System.out.println("Nenhum inscrito.");
                    } else {
                        inscricoes.forEach(i -> System.out.println(i.getParticipante().getNomeCompleto() + " (ID Inscrição: " + i.getId() + ", Status: " + i.getStatus() + (i.isPresencaConfirmada() ? ", PRESENTE" : "") + ")"));
                    }
                    break;
                case 2:
                    String inscricaoId = lerString("ID da Inscrição para confirmar presença: ");
                    facade.confirmarPresenca(inscricaoId, participanteLogado.getId());
                    System.out.println("Presença confirmada.");
                    break;
                case 3:
                    System.out.println("Editando detalhes (deixe em branco para não alterar):");
                    String novoNome = lerStringOpcional("Novo Nome (" + eventoSelecionado.getNome() + "): ");
                    String novaDesc = lerStringOpcional("Nova Descrição: ");
                    // Adicionar mais campos para edição conforme necessário
                    facade.atualizarDetalhesEvento(eventoId, participanteLogado.getId(), novoNome, novaDesc, null, null, null, null);
                    System.out.println("Detalhes do evento atualizados.");
                    break;
                case 4:
                    LocalDate subInicio = lerData("Nova Data de Início da Submissão (yyyy-MM-dd): ");
                    LocalDate subFim = lerData("Nova Data de Fim da Submissão (yyyy-MM-dd): ");
                    facade.definirPeriodoSubmissaoTrabalhos(eventoId, participanteLogado.getId(), subInicio, subFim);
                    System.out.println("Período de submissão atualizado.");
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao gerenciar evento: " + e.getMessage());
        }
    }

    private static void designarAvaliador() {
        System.out.println("\n--- Designar Avaliador para Trabalho ---");
        // Listar eventos do organizador
        List<Evento> meusEventos = facade.listarEventosPorOrganizador(participanteLogado.getId());
        if (meusEventos.isEmpty()) {
            System.out.println("Nenhum evento seu para gerenciar trabalhos.");
            return;
        }
        meusEventos.forEach(e -> System.out.println(e.getId() + " - " + e.getNome()));
        String eventoId = lerString("ID do Evento do trabalho: ");

        List<Trabalho> trabalhos = facade.listarTrabalhosPorEvento(eventoId).stream()
                .filter(t -> t.getStatus() == StatusTrabalho.SUBMETIDO || t.getStatus() == StatusTrabalho.EM_AVALIACAO)
                .collect(Collectors.toList());

        if (trabalhos.isEmpty()) {
            System.out.println("Nenhum trabalho pendente de avaliação neste evento.");
            return;
        }
        System.out.println("Trabalhos pendentes:");
        trabalhos.forEach(t -> System.out.println(t.getId() + " - " + t.getTitulo()));
        String trabalhoId = lerString("ID do Trabalho: ");

        System.out.println("Avaliadores disponíveis:");
        facade.listarTodosParticipantes().stream()
                .filter(p -> p.getTipoPerfil() == TipoPerfil.AVALIADOR)
                .forEach(p -> System.out.println(p.getId() + " - " + p.getNomeCompleto()));
        String avaliadorId = lerString("ID do Avaliador: ");

        try {
            facade.designarAvaliadorParaTrabalho(trabalhoId, avaliadorId, participanteLogado.getId());
            System.out.println("Avaliador designado com sucesso!");
        } catch (Exception e) {
            System.err.println("Erro ao designar avaliador: " + e.getMessage());
        }
    }

    private static void atualizarStatusTrabalho() {
        System.out.println("\n--- Atualizar Status de Trabalho ---");
        // Similar a designarAvaliador, listar eventos, depois trabalhos
        List<Evento> meusEventos = facade.listarEventosPorOrganizador(participanteLogado.getId());
        if (meusEventos.isEmpty()) {
            System.out.println("Nenhum evento seu para gerenciar trabalhos.");
            return;
        }
        meusEventos.forEach(e -> System.out.println(e.getId() + " - " + e.getNome()));
        String eventoId = lerString("ID do Evento do trabalho: ");

        List<Trabalho> trabalhos = facade.listarTrabalhosPorEvento(eventoId);
        if (trabalhos.isEmpty()) {
            System.out.println("Nenhum trabalho neste evento.");
            return;
        }
        trabalhos.forEach(t -> System.out.println(t.getId() + " - " + t.getTitulo() + " (Status Atual: " + t.getStatus() + ")"));
        String trabalhoId = lerString("ID do Trabalho para atualizar status: ");
        StatusTrabalho novoStatus = lerStatusTrabalho();
        try {
            facade.atualizarStatusTrabalho(trabalhoId, novoStatus, participanteLogado.getId());
            System.out.println("Status do trabalho atualizado para " + novoStatus);
        } catch (Exception e) {
            System.err.println("Erro ao atualizar status: " + e.getMessage());
        }
    }

    private static void emitirCertificadosDoEvento() {
        System.out.println("\n--- Emitir Certificados do Evento ---");
        List<Evento> meusEventos = facade.listarEventosPorOrganizador(participanteLogado.getId());
        if (meusEventos.isEmpty()) {
            System.out.println("Nenhum evento seu para emitir certificados.");
            return;
        }
        meusEventos.forEach(e -> System.out.println(e.getId() + " - " + e.getNome()));
        String eventoId = lerString("ID do Evento para emitir certificados: ");

        System.out.println("Emitindo certificados...");
        try {
            List<Certificado> cParticipacao = facade.emitirCertificadosParticipacaoEvento(eventoId);
            System.out.println(cParticipacao.size() + " certificados de participação emitidos/verificados.");

            List<Certificado> cApresentacao = facade.emitirCertificadosApresentacaoTrabalhoEvento(eventoId);
            System.out.println(cApresentacao.size() + " certificados de apresentação de trabalho emitidos/verificados.");

            Certificado cOrg = facade.emitirCertificadoOrganizadorEvento(eventoId, participanteLogado.getId());
            System.out.println("Certificado de organização emitido para você (ID: " + cOrg.getId() + ")");

        } catch (Exception e) {
            System.err.println("Erro ao emitir certificados: " + e.getMessage());
        }
    }

    // --- Métodos do Avaliador ---
    private static void listarTrabalhosParaAvaliar() {
        System.out.println("\n--- Trabalhos Designados para Avaliação (Simplificado) ---");
        // Simplificação: lista trabalhos em avaliação ou que o avaliador já tenha uma avaliação (mesmo que não finalizada)
        // Uma implementação completa usaria uma lista de designações explícitas.
        try {
            // Tentar listar todos os trabalhos EM_AVALIACAO como uma aproximação
            List<Trabalho> todosTrabalhos = new ArrayList<>();
            facade.listarTodosEventos().forEach(evento
                    -> todosTrabalhos.addAll(facade.listarTrabalhosPorEvento(evento.getId()))
            );

            List<Trabalho> paraAvaliar = todosTrabalhos.stream()
                    .filter(t -> t.getStatus() == StatusTrabalho.EM_AVALIACAO)
                    // Adicionar filtro se houver lista de avaliadores designados no trabalho
                    // .filter(t -> t.getAvaliadoresDesignados().contains(participanteLogado)) 
                    .collect(Collectors.toList());

            if (paraAvaliar.isEmpty()) {
                System.out.println("Nenhum trabalho atualmente designado para você ou em status de avaliação.");
            } else {
                paraAvaliar.forEach(t -> System.out.println(t.getId() + " - Título: " + t.getTitulo() + " (Evento: " + t.getEvento().getNome() + ")"));
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar trabalhos para avaliação: " + e.getMessage());
        }
    }

    private static void registrarAvaliacao() {
        System.out.println("\n--- Registrar Avaliação de Trabalho ---");
        listarTrabalhosParaAvaliar(); // Mostrar trabalhos que podem ser avaliados
        String trabalhoId = lerString("ID do Trabalho a avaliar: ");
        double nota = lerDouble("Nota (0.0 - 10.0): ");
        String parecer = lerString("Parecer/Comentários: ");
        try {
            Avaliacao aval = facade.registrarAvaliacao(trabalhoId, participanteLogado.getId(), nota, parecer);
            System.out.println("Avaliação registrada com sucesso! ID da Avaliação: " + aval.getId());
        } catch (Exception e) {
            System.err.println("Erro ao registrar avaliação: " + e.getMessage());
        }
    }

    // --- Helpers de Input ---
    private static String lerString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private static String lerStringOpcional(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine();
        return input.trim().isEmpty() ? null : input;
    }

    private static int lerInteiro(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, insira um número inteiro.");
            }
        }
    }

    private static double lerDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, insira um número decimal (use '.' como separador).");
            }
        }
    }

    private static LocalDate lerData(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return LocalDate.parse(scanner.nextLine(), DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("Formato de data inválido. Use yyyy-MM-dd.");
            }
        }
    }

    private static LocalDate lerDataOpcional(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine();
        if (input.trim().isEmpty()) {
            return null;
        }
        while (true) {
            try {
                return LocalDate.parse(input, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("Formato de data inválido. Use yyyy-MM-dd ou deixe em branco.");
                System.out.print(prompt); // Pergunta novamente
                input = scanner.nextLine();
                if (input.trim().isEmpty()) {
                    return null;
                }
            }
        }
    }

    private static StatusTrabalho lerStatusTrabalho() {
        while (true) {
            System.out.print("Novo Status do Trabalho (");
            for (StatusTrabalho st : StatusTrabalho.values()) {
                System.out.print(st.name() + " ");
            }
            System.out.print("): ");
            String input = scanner.nextLine().toUpperCase();
            try {
                return StatusTrabalho.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Status de trabalho inválido.");
            }
        }
    }

    // --- Dados Iniciais para Teste ---
    private static void seedInitialData() {
        try {
            Participante org = facade.cadastrarParticipante("Organizador Chefe", "org@sgea.com", "IFNMG", TipoPerfil.ORGANIZADOR);
            System.out.println("  Criado Organizador: " + org.getNomeCompleto() + " (ID: " + org.getId() + ")");

            Participante aluno1 = facade.cadastrarParticipante("Aluno Fulano", "aluno1@sgea.com", "IFNMG", TipoPerfil.PARTICIPANTE);
            System.out.println("  Criado Aluno: " + aluno1.getNomeCompleto() + " (ID: " + aluno1.getId() + ")");

            Participante aluno2 = facade.cadastrarParticipante("Aluna Ciclana", "aluno2@sgea.com", "IFNMG", TipoPerfil.PARTICIPANTE);
            System.out.println("  Criado Aluno: " + aluno2.getNomeCompleto() + " (ID: " + aluno2.getId() + ")");

            Participante aluno3 = facade.cadastrarParticipante("Aluno Beltrano", "aluno3@sgea.com", "IFNMG", TipoPerfil.PARTICIPANTE);
            System.out.println("  Criado Aluno: " + aluno3.getNomeCompleto() + " (ID: " + aluno3.getId() + ")");

            Participante aluno4 = facade.cadastrarParticipante("Aluna Daniela", "aluno4@sgea.com", "IFNMG", TipoPerfil.PARTICIPANTE);
            System.out.println("  Criado Aluno: " + aluno4.getNomeCompleto() + " (ID: " + aluno4.getId() + ")");

            Participante aluno5 = facade.cadastrarParticipante("Aluno Eduardo", "aluno5@sgea.com", "IFNMG", TipoPerfil.PARTICIPANTE);
            System.out.println("  Criado Aluno: " + aluno5.getNomeCompleto() + " (ID: " + aluno5.getId() + ")");

            Participante avaliador1 = facade.cadastrarParticipante("Prof. Avalion", "avaliador1@sgea.com", "IFNMG", TipoPerfil.AVALIADOR);
            System.out.println("  Criado Avaliador: " + avaliador1.getNomeCompleto() + " (ID: " + avaliador1.getId() + ")");

            Participante avaliador2 = facade.cadastrarParticipante("Prof. Juliana Silva", "avaliador2@sgea.com", "IFNMG", TipoPerfil.AVALIADOR);
            System.out.println("  Criado Avaliador: " + avaliador2.getNomeCompleto() + " (ID: " + avaliador2.getId() + ")");

            Participante avaliador3 = facade.cadastrarParticipante("Prof. Marcos Andrade", "avaliador3@sgea.com", "IFNMG", TipoPerfil.AVALIADOR);
            System.out.println("  Criado Avaliador: " + avaliador3.getNomeCompleto() + " (ID: " + avaliador3.getId() + ")");

            Evento evento1 = facade.cadastrarEvento(
                    "Semana Acadêmica de TI",
                    "Palestras e minicursos sobre tecnologia.",
                    LocalDate.now().plusDays(10),
                    LocalDate.now().plusDays(12),
                    "Auditório Principal IFNMG",
                    2,
                    org.getId(),
                    LocalDate.now().plusDays(1),
                    LocalDate.now().plusDays(8)
            );
            System.out.println("  Criado Evento: " + evento1.getNome() + " (ID: " + evento1.getId() + ")");

            Evento evento2 = facade.cadastrarEvento(
                    "Simpósio de Inovação e Empreendedorismo",
                    "Atividades voltadas para empreendedorismo, startups e inovação.",
                    LocalDate.now().plusDays(20),
                    LocalDate.now().plusDays(22),
                    "Sala de Conferências IFNMG",
                    80,
                    org.getId(),
                    LocalDate.now().plusDays(5),
                    LocalDate.now().plusDays(18)
            );
            System.out.println("  Criado Evento: " + evento2.getNome() + " (ID: " + evento2.getId() + ")");

            Evento evento3 = facade.cadastrarEvento(
                    "Workshop de Programação",
                    "Oficinas práticas de linguagens e desenvolvimento de software.",
                    LocalDate.now().plusDays(30),
                    LocalDate.now().plusDays(31),
                    "Laboratório de Informática IFNMG",
                    60,
                    org.getId(),
                    LocalDate.now().plusDays(10),
                    LocalDate.now().plusDays(28)
            );
            System.out.println("  Criado Evento: " + evento3.getNome() + " (ID: " + evento3.getId() + ")");

            facade.inscreverEmEvento(aluno1.getId(), evento1.getId());
            System.out.println("  " + aluno1.getNomeCompleto() + " inscrito na " + evento1.getNome());
            facade.inscreverEmEvento(aluno2.getId(), evento1.getId());
            System.out.println("  " + aluno2.getNomeCompleto() + " inscrito na " + evento1.getNome());

        } catch (Exception e) {
            System.err.println("Erro ao semear dados: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
