package br.com.claudiocarige.desafio.adapter.in.web.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("CustomerController - Testes de integração (segurança e paginação)")
class CustomerControllerIntegrationTest {

    private static final String USER = "user-dantum";
    private static final String USER_PASSWORD = "user123";
    private static final String ADMIN = "admin-dantum";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String CPF_NOVO_CLIENTE = "11144477735";

    @Autowired
    private MockMvc mockMvc;

    @Nested
    @DisplayName("Autenticação")
    class Autenticacao {

        @Test
        @DisplayName("Deve retornar 401 quando a requisição não possui credenciais")
        void deveRetornar401SemCredenciais() throws Exception {
            mockMvc.perform(get("/customers/search"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Autorização por role")
    class AutorizacaoPorRole {

        @Test
        @DisplayName("USER não pode criar cliente (POST) e deve receber 403")
        void userNaoPodeCriarCliente() throws Exception {
            String body = """
                    {"name":"Novo Cliente","cpf":"%s","email":"novo@email.com"}
                    """.formatted(CPF_NOVO_CLIENTE);

            mockMvc.perform(post("/customers/create")
                            .with(httpBasic(USER, USER_PASSWORD))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("ADMIN pode criar cliente (POST) e deve receber 201")
        void adminPodeCriarCliente() throws Exception {
            String body = """
                    {"name":"Novo Cliente","cpf":"%s","email":"novo@email.com"}
                    """.formatted(CPF_NOVO_CLIENTE);

            mockMvc.perform(post("/customers/create")
                            .with(httpBasic(ADMIN, ADMIN_PASSWORD))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.cpf").value("***.***.777-35"));
        }

        @Test
        @DisplayName("USER não pode atualizar cliente (PUT) e deve receber 403")
        void userNaoPodeAtualizarCliente() throws Exception {
            mockMvc.perform(put("/customers/update/{id}", java.util.UUID.randomUUID())
                            .with(httpBasic(USER, USER_PASSWORD))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"name":"Qualquer","cpf":"85541025095","email":"qualquer@email.com","status":"ACTIVE"}
                                    """))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("ADMIN também pode consultar (GET)")
        void adminPodeConsultar() throws Exception {
            mockMvc.perform(get("/customers/search")
                            .with(httpBasic(ADMIN, ADMIN_PASSWORD)))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Paginação e filtros")
    class PaginacaoEFiltros {

        @Test
        @DisplayName("GET /customers/search deve retornar metadados de paginação corretos")
        void deveRetornarMetadadosDePaginacao() throws Exception {
            mockMvc.perform(get("/customers/search")
                            .param("page", "0")
                            .param("size", "5")
                            .with(httpBasic(USER, USER_PASSWORD)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.page").value(0))
                    .andExpect(jsonPath("$.size").value(5))
                    .andExpect(jsonPath("$.content.length()").value(5))
                    .andExpect(jsonPath("$.totalElements").value(12))
                    .andExpect(jsonPath("$.totalPages").value(3))
                    .andExpect(jsonPath("$.hasNext").value(true))
                    .andExpect(jsonPath("$.hasPrevious").value(false));
        }

        @Test
        @DisplayName("GET /customers/search?name= deve filtrar clientes pelo nome informado")
        void deveFiltrarClientesPorNome() throws Exception {
            mockMvc.perform(get("/customers/search")
                            .param("name", "Maria")
                            .with(httpBasic(USER, USER_PASSWORD)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].name").value("Maria Pimentel"))
                    .andExpect(jsonPath("$.totalElements").value(1));
        }

        @Test
        @DisplayName("GET /customers?status=ACTIVE deve filtrar clientes pelo status informado")
        void deveFiltrarClientesPorStatus() throws Exception {
            mockMvc.perform(get("/customers")
                            .param("status", "ACTIVE")
                            .with(httpBasic(USER, USER_PASSWORD)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalElements").value(6));
        }

        @Test
        @DisplayName("GET /customers/search/{id} com id inexistente deve retornar 404")
        void deveRetornar404QuandoClienteNaoExiste() throws Exception {
            mockMvc.perform(get("/customers/search/{id}", java.util.UUID.randomUUID())
                            .with(httpBasic(USER, USER_PASSWORD)))
                    .andExpect(status().isNotFound());
        }
    }
}
