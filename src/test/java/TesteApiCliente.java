import io.restassured.http.ContentType;
import io.restassured.response.Validatable;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.CoreMatchers.not;

public class TesteApiCliente {

    private String servicoCliente = "http://localhost:8080";
    private String endpointCliente = "/cliente";
    private String endpointApagaTodosClientes ="/apagaTodos";
    private static final String listaClientesVazia ="{}";

    @Test
    @DisplayName("Quando consultar a lista de clientes sem cadastrar nenhum. Então na resposta não deve retornar nenhum cliente cadastrado")
    public void consultarListaClientesVazia() {
        apagarTodosClientes();
        pegaTodosClientes()
                .statusCode(200)
                .body(equalTo(listaClientesVazia));
   }

    @Test
    @DisplayName("Quando cadastrar um cliente. Então na resposta deve retornar os dados do cliente cadastrado.")
    public void cadastraCliente() {

        Cliente clienteParaCadastrar = new Cliente("Taina Mendes", 33, 1987);

       postaCliente(clienteParaCadastrar)
                .statusCode(201)
                .body("1987.nome",equalTo("Taina Mendes"))
                .body("1987.idade", equalTo(33))
                .body("1987.id", equalTo(1987));
      apagarTodosClientes();

    }

    @Test
    @DisplayName("Quando atualizar os dados do cliente. Então na resposta deve retornar os dados do cliente atualizado.")
    public void atualizarDadosCliente(){

        Cliente clienteParaCadastrar = new Cliente("Lisa", 10, 1987);
        postaCliente(clienteParaCadastrar);

        Cliente clienteParaAtualizar = new Cliente("Lisa Marie Simpson", 8, 1987);
        atualizaCliente(clienteParaAtualizar)
                .statusCode(200)
                .body("1987.id", equalTo(1987))
                .body("1987.nome", equalTo("Lisa Marie Simpson"))
                .body("1987.idade", equalTo(8));
        apagarTodosClientes();
    }

    @Test
    @DisplayName("Quando pesquisar um cliente por ID. Então na resposta deve retornar os dados desse cliente específico.")
    public void consultarClientePorID(){

        Cliente cliente = new Cliente("Marge Simpson", 38, 1987);
        postaCliente(cliente);

        pegaClientePorId(cliente)
                .statusCode(200)
                .body("nome", equalTo("Marge Simpson"))
                .body("idade", equalTo(38))
                .body("id", equalTo(1987))
                .body("risco", equalTo(0));

        apagarTodosClientes();
    }

    @Test
    @DisplayName("Quando apagar um cliente. Então seus dados devem ser removidos com sucesso.")
    public void deletarClientePorID(){

        Cliente cliente = new Cliente("Maggie Simpson", 1, 1987);
        postaCliente(cliente);

        apagaClienteporId(cliente)
               .statusCode(200)
               .body(equalTo("CLIENTE REMOVIDO: { NOME: Maggie Simpson, IDADE: 1, ID: 1987 }"))
               .assertThat().body(not(contains("Maggie Simpson")));
    }

    /**
     * (GET) Pega todos os clientes cadastrados na API e
     * @return uma lista com todos os clientes wrapped no tipo de resposta do restAssured
     */
    private ValidatableResponse pegaTodosClientes(){
        return  given()
                .contentType(ContentType.JSON)
                .when()
                .get(servicoCliente)
                .then();
   }

    /**
     * (POST) Cadastra cliente para a nossa API de Testes.
     * @param clienteParaPostar
     */
   private ValidatableResponse postaCliente(Cliente clienteParaPostar){
       return given()
               .contentType(ContentType.JSON)
               .body(clienteParaPostar)
               .when()
               .post(servicoCliente + endpointCliente)
               .then();
   }

    /**
     * Atualizar dados do cliente na nossa API de Testes.
     * @param clienteParaAtualizar
     */
    private ValidatableResponse atualizaCliente (Cliente clienteParaAtualizar){
        return given()
                .contentType(ContentType.JSON)
                .body(clienteParaAtualizar)
                .when()
                .put(servicoCliente + endpointCliente)
                .then();
    }

    /**
     * Pegar cliente específico por ID.
     * @param consultarCliente
     * @return
     */
    private ValidatableResponse pegaClientePorId(Cliente consultarCliente){
        return given()
             .contentType(ContentType.JSON)
             .when()
             .get(servicoCliente + endpointCliente +"/" + consultarCliente.getId())
             .then();
   }

    /**
     * Apagar um client específico por ID
     * @param clienteApagar
     * @return
     */
    private ValidatableResponse apagaClienteporId (Cliente clienteApagar){
        return given()
               .contentType(ContentType.JSON)
                .when()
                .delete(servicoCliente + endpointCliente+"/"+ clienteApagar.getId())
                .then();
    }


    /** Método de apoio para apagar todos os clientes do servidor.
     * Usado apenas para testes.
     **/
    public void apagarTodosClientes() {
        //deletar dados
        given()
                .contentType(ContentType.JSON)
        .when()
                .delete(servicoCliente + endpointCliente + endpointApagaTodosClientes)
        .then()
                .statusCode(200)
                .body(equalTo(listaClientesVazia));
    }

}