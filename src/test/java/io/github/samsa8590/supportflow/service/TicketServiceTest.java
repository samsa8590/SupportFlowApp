package io.github.samsa8590.supportflow.service;

import io.github.samsa8590.supportflow.dto.request.AssignTicketRequest;
import io.github.samsa8590.supportflow.dto.request.CreateTicketRequest;
import io.github.samsa8590.supportflow.dto.request.UpdateTicketStatusRequest;
import io.github.samsa8590.supportflow.dto.response.TicketResponse;
import io.github.samsa8590.supportflow.entity.Employee;
import io.github.samsa8590.supportflow.entity.Ticket;
import io.github.samsa8590.supportflow.enums.Priority;
import io.github.samsa8590.supportflow.enums.Role;
import io.github.samsa8590.supportflow.enums.TicketStatus;
import io.github.samsa8590.supportflow.exception.BusinessException;
import io.github.samsa8590.supportflow.exception.InvalidStatusTransitionException;
import io.github.samsa8590.supportflow.exception.ResourceNotFoundException;
import io.github.samsa8590.supportflow.mapper.EmployeeMapper;
import io.github.samsa8590.supportflow.mapper.TicketMapper;
import io.github.samsa8590.supportflow.repository.EmployeeRepository;
import io.github.samsa8590.supportflow.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    private TicketService service;

    @BeforeEach
    void setUp() {
        service = new TicketService(ticketRepository, employeeRepository,
                new TicketMapper(new EmployeeMapper()));
    }

    // ---------- create ----------

    @Test
    @DisplayName("create: приоритет по умолчанию MEDIUM, статус OPEN")
    void createUsesDefaults() {
        Employee author = employee(1L, Role.EMPLOYEE, true);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(author));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));

        TicketResponse response = service.create(
                new CreateTicketRequest("VPN", "Не подключается", null, 1L));

        ArgumentCaptor<Ticket> captor = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketRepository).save(captor.capture());
        Ticket saved = captor.getValue();

        assertThat(saved.getPriority()).isEqualTo(Priority.MEDIUM);
        assertThat(saved.getStatus()).isEqualTo(TicketStatus.OPEN);
        assertThat(saved.getAuthor()).isSameAs(author);
        assertThat(response.title()).isEqualTo("VPN");
    }

    @Test
    @DisplayName("create: переданный приоритет сохраняется")
    void createKeepsGivenPriority() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee(1L, Role.EMPLOYEE, true)));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));

        TicketResponse response = service.create(
                new CreateTicketRequest("Сервер лежит", "502", Priority.CRITICAL, 1L));

        assertThat(response.priority()).isEqualTo(Priority.CRITICAL);
    }

    @Test
    @DisplayName("create: автор не найден -> 404")
    void createAuthorNotFound() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(new CreateTicketRequest("a", "b", null, 99L)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(ticketRepository, never()).save(any());
    }

    @Test
    @DisplayName("create: неактивный автор не может создавать тикеты")
    void createInactiveAuthor() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee(1L, Role.EMPLOYEE, false)));

        assertThatThrownBy(() -> service.create(new CreateTicketRequest("a", "b", null, 1L)))
                .isInstanceOf(BusinessException.class);

        verify(ticketRepository, never()).save(any());
    }

    // ---------- getById ----------

    @Test
    @DisplayName("getById: тикет не найден -> 404")
    void getByIdNotFound() {
        when(ticketRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(5L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ---------- changeStatus ----------

    @Test
    @DisplayName("changeStatus: OPEN -> IN_PROGRESS с исполнителем проходит")
    void changeStatusValid() {
        Ticket ticket = ticket(1L, TicketStatus.OPEN, employee(2L, Role.AGENT, true));
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        TicketResponse response = service.changeStatus(1L,
                new UpdateTicketStatusRequest(TicketStatus.IN_PROGRESS));

        assertThat(response.status()).isEqualTo(TicketStatus.IN_PROGRESS);
        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.IN_PROGRESS);
        verify(ticketRepository, never()).save(any());
    }

    @Test
    @DisplayName("changeStatus: OPEN -> CLOSED запрещён")
    void changeStatusInvalidTransition() {
        Ticket ticket = ticket(1L, TicketStatus.OPEN, null);
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        assertThatThrownBy(() -> service.changeStatus(1L, new UpdateTicketStatusRequest(TicketStatus.CLOSED)))
                .isInstanceOf(InvalidStatusTransitionException.class)
                .hasMessageContaining("OPEN")
                .hasMessageContaining("CLOSED");

        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.OPEN);
    }

    @Test
    @DisplayName("changeStatus: в IN_PROGRESS нельзя без исполнителя")
    void changeStatusRequiresAssignee() {
        Ticket ticket = ticket(1L, TicketStatus.OPEN, null);
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        assertThatThrownBy(() -> service.changeStatus(1L, new UpdateTicketStatusRequest(TicketStatus.IN_PROGRESS)))
                .isInstanceOf(BusinessException.class)
                .isNotInstanceOf(InvalidStatusTransitionException.class)
                .hasMessageContaining("Assign");

        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.OPEN);
    }

    @Test
    @DisplayName("changeStatus: из закрытого тикета выхода нет")
    void changeStatusFromClosed() {
        Ticket ticket = ticket(1L, TicketStatus.CLOSED, employee(2L, Role.AGENT, true));
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        assertThatThrownBy(() -> service.changeStatus(1L, new UpdateTicketStatusRequest(TicketStatus.OPEN)))
                .isInstanceOf(InvalidStatusTransitionException.class);
    }

    @Test
    @DisplayName("changeStatus: тикет не найден -> 404")
    void changeStatusNotFound() {
        when(ticketRepository.findById(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.changeStatus(7L, new UpdateTicketStatusRequest(TicketStatus.RESOLVED)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ---------- assign ----------

    @Test
    @DisplayName("assign: активный агент назначается")
    void assignAgent() {
        Ticket ticket = ticket(1L, TicketStatus.OPEN, null);
        Employee agent = employee(2L, Role.AGENT, true);
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(agent));

        TicketResponse response = service.assign(1L, new AssignTicketRequest(2L));

        assertThat(ticket.getAssignee()).isSameAs(agent);
        assertThat(response.assignee().id()).isEqualTo(2L);
    }

    @Test
    @DisplayName("assign: администратор тоже может быть исполнителем")
    void assignAdmin() {
        Ticket ticket = ticket(1L, TicketStatus.OPEN, null);
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(employeeRepository.findById(3L)).thenReturn(Optional.of(employee(3L, Role.ADMIN, true)));

        service.assign(1L, new AssignTicketRequest(3L));

        assertThat(ticket.getAssignee().getId()).isEqualTo(3L);
    }

    @Test
    @DisplayName("assign: обычного сотрудника назначить нельзя")
    void assignRegularEmployee() {
        Ticket ticket = ticket(1L, TicketStatus.OPEN, null);
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee(1L, Role.EMPLOYEE, true)));

        assertThatThrownBy(() -> service.assign(1L, new AssignTicketRequest(1L)))
                .isInstanceOf(BusinessException.class);

        assertThat(ticket.getAssignee()).isNull();
    }

    @Test
    @DisplayName("assign: неактивного агента назначить нельзя")
    void assignInactiveAgent() {
        Ticket ticket = ticket(1L, TicketStatus.OPEN, null);
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(employee(2L, Role.AGENT, false)));

        assertThatThrownBy(() -> service.assign(1L, new AssignTicketRequest(2L)))
                .isInstanceOf(BusinessException.class);

        assertThat(ticket.getAssignee()).isNull();
    }

    @Test
    @DisplayName("assign: закрытому тикету исполнителя не назначить")
    void assignClosedTicket() {
        Ticket ticket = ticket(1L, TicketStatus.CLOSED, null);
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));


        assertThatThrownBy(() -> service.assign(1L, new AssignTicketRequest(2L)))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("assign: исполнитель не найден -> 404")
    void assignEmployeeNotFound() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket(1L, TicketStatus.OPEN, null)));
        when(employeeRepository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.assign(1L, new AssignTicketRequest(42L)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ---------- помощники для создания тестовых данных ----------

    private static Employee employee(Long id, Role role, boolean active) {
        Employee e = new Employee();
        e.setId(id);
        e.setFullName("Employee " + id);
        e.setEmail("e" + id + "@test.local");
        e.setPasswordHash("x");
        e.setRole(role);
        e.setActive(active);
        return e;
    }

    private static Ticket ticket(Long id, TicketStatus status, Employee assignee) {
        Ticket t = new Ticket();
        t.setId(id);
        t.setTitle("Title");
        t.setDescription("Description");
        t.setStatus(status);
        t.setAuthor(employee(1L, Role.EMPLOYEE, true));
        t.setAssignee(assignee);
        return t;
    }
}
