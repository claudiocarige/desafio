package br.com.claudiocarige.desafio.adapter.in.web.controllers;

import br.com.claudiocarige.desafio.adapter.in.web.dto.CreateCustomerRequest;
import br.com.claudiocarige.desafio.adapter.in.web.dto.CustomerPageResponse;
import br.com.claudiocarige.desafio.adapter.in.web.dto.CustomerResponse;
import br.com.claudiocarige.desafio.adapter.in.web.dto.UpdateCustomerRequest;
import br.com.claudiocarige.desafio.adapter.in.web.exceptions.ApiError;
import br.com.claudiocarige.desafio.application.dto.CustomerScoreDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Contrato OpenAPI/Swagger do recurso Customers.
 * As anotações de documentação e de mapeamento HTTP ficam aqui,
 * mantendo {@link CustomerController} livre desse detalhe de infraestrutura.
 */
@Tag(name = "Customers", description = "Gestão de clientes e consulta de score")
@RequestMapping("/customers")
public interface CustomerControllerApi {

    @Operation(summary = "Cria um novo cliente", description = "Requer perfil ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso",
                    content = @Content(schema = @Schema(implementation = CustomerResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autorizado",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description = "Sem permissão",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "422", description = "Dados inválidos ou CPF already exists",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/create")
    ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CreateCustomerRequest request);

    @Operation(summary = "Busca um cliente pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de clientes",
                    content = @Content(schema = @Schema(implementation = CustomerPageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Não autorizado",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/search/{id}")
    ResponseEntity<CustomerResponse> findCustomerById(
            @Parameter(description = "ID do cliente") @PathVariable UUID id);

    @Operation(summary = "Lista clientes de forma paginada")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de clientes",
                    content = @Content(schema = @Schema(implementation = CustomerPageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Não autorizado",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/search")
    ResponseEntity<CustomerPageResponse> searchCustomers(
            @Parameter(description = "Número da página (0-based)") @RequestParam(defaultValue = "0", required = false) Integer page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "20", required = false) Integer size,
            @Parameter(description = "Nome do cliente (opcional)", example = "Silva")
            @RequestParam(required = false)
            @Size(max = 50, message = "O nome para busca não pode exceder 50 caracteres")
            @Pattern(regexp = "^[a-zA-Z0-9À-ÿ\\s'-]*$", message = "O nome contém caracteres inválidos")
            String name);

    @Operation(summary = "Atualiza um cliente existente", description = "Requer perfil ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = CustomerResponse.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Não autorizado",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description = "Sem permissão",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "422", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/update/{id}")
    ResponseEntity<CustomerResponse> updateCustomer(
            @Parameter(description = "ID do cliente") @PathVariable UUID id,
            @Valid @RequestBody UpdateCustomerRequest request);

    @Operation(summary = "Consulta o score do cliente em serviço externo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Score obtido com sucesso",
                    content = @Content(schema = @Schema(implementation = CustomerScoreDto.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "502", description = "Falha ao consultar o serviço externo de score",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/{id}/score")
    ResponseEntity<CustomerScoreDto> getCustomerScore(
            @Parameter(description = "ID do cliente") @PathVariable UUID id);

    @Operation(summary = "Lista clientes filtrando por status, com paginação")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de clientes",
                    content = @Content(schema = @Schema(implementation = CustomerPageResponse.class))),
            @ApiResponse(responseCode = "422", description = "Status inválido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping
    ResponseEntity<CustomerPageResponse> findCustomersByStatus(
            @Parameter(description = "Status do cliente: ACTIVE, INACTIVE ou BLOCKED") @RequestParam String status,
            @Parameter(description = "Número da página (0-based)") @RequestParam(defaultValue = "0", required = false) Integer page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "20", required = false) Integer size);

    @Operation(summary = "Endpoint informativo de exclusão",
            description = "Clientes não podem ser deletados fisicamente; utilize PUT /customers/update/{id} para alterar o status.")
    @ApiResponse(responseCode = "422", description = "Exclusão física não suportada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @DeleteMapping("/delete/{id}")
    ResponseEntity<Void> deleteCustomer(
            @Parameter(description = "ID do cliente") @PathVariable UUID id);
}
