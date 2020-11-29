import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.CoreMatchers.not;

public class TesteApiCliente {

    //URIs utilizadas.
    private String servicoCliente = "http://localhost:8080";
    private String endpointCliente = "/cliente";
    private String endpointApagaTodosClientes ="/apagaTodos";
    private static final String listaClientesVazia ="{}";

    @Test
    @DisplayName("Quando consultar a lista de clientes sem cadastrar nenhum. Então na resposta não deve retornar nenhum cliente cadastrado")
    public void consultarListaClientesVazia() {
        //limpardados
        apagarTodosClientes();

        //GET todos os Clientes e verifica a lista vazia.
        given()
                .contentType(ContentType.JSON)
        .when()
                .get(servicoCliente)
        .then()
                .statusCode(200)
                .body(equalTo(listaClientesVazia));

    }

    @Test
    @DisplayName("Quando cadastrar um cliente. Então na resposta deve retornar os dados do cliente cadastrado.")
    public void cadastraCliente() {
        //limpardados
        apagarTodosClientes();

        // Resposta esperada
        String respostaEsperada = "{\"1987\":{\"nome\":\"Taina Mendes\",\"idade\":33,\"id\":1987,\"risco\":0}}";

        Cliente clienteParaCadastrar = new Cliente();

        /**
         * Os dados para cadastros foram substituidos por um objeto e são serializados para serem enviados para API.
         * O restAssured usa o Jackson implicitamente para essa serialização.
         */

        clienteParaCadastrar.setNome("Taina Mendes");
        clienteParaCadastrar.setIdade(33);
        clienteParaCadastrar.setId(1987);

        //POST Cadastrar um cliente
        given()
                .contentType(ContentType.JSON)
                .body(clienteParaCadastrar)
        .when()
                .post(servicoCliente + endpointCliente)
        .then()
                .statusCode(201)
                .body(equalTo(respostaEsperada));
                //verificando a resposta inteira que está como String e fazemos o match dela inteira

    }

    @Test
    @DisplayName("Quando atualizar os dados do cliente. Então na resposta deve retornar os dados do cliente atualizado.")
    public void atualizarDadosCliente(){

        Cliente clienteParaCadastrar = new Cliente ();

        clienteParaCadastrar.setNome("Lisa");
        clienteParaCadastrar.setIdade(10);
        clienteParaCadastrar.setId(1987);

        Cliente clienteParaAtualizar = new Cliente();
        clienteParaAtualizar.setNome("Lisa Marie Simpson");
        clienteParaAtualizar.setIdade(8);
        clienteParaAtualizar.setId(1987);

        //criar cadastro
        given()
                .contentType(ContentType.JSON)
        .body(clienteParaCadastrar)
                .when()
        .post(servicoCliente + endpointCliente);

        //atualizar dados
        given()
                .contentType(ContentType.JSON)
        .body(clienteParaAtualizar)
                .when()
                .put(servicoCliente + endpointCliente)
        .then()
                .statusCode(200)
                .body("1987.id", equalTo(1987))
                .body("1987.nome", equalTo("Lisa Marie Simpson"))
                .body("1987.idade", equalTo(8));
                //verificando elemento por elemento do Json da resposta.
        //limpardados
        apagarTodosClientes();
    }

    @Test
    @DisplayName("Quando pesquisar um cliente por ID. Então na resposta deve retornar os dados desse cliente específico.")
    public void consultarClientePorID(){

        Cliente clienteParaCadastrar = new Cliente ();

        clienteParaCadastrar.setNome("Marge Simpson");
        clienteParaCadastrar.setIdade(38);
        clienteParaCadastrar.setId(1987);

        //criar cadastro
        given()
                .contentType(ContentType.JSON)
        .body(clienteParaCadastrar)
                .when()
        .post(servicoCliente + endpointCliente);

        //buscar cliente por id
        given()
                .contentType(ContentType.JSON)
        .when()
                .get(servicoCliente + endpointCliente +"/" + clienteParaCadastrar.getId())
        .then()
                .statusCode(200)
                .body("nome", equalTo("Marge Simpson"))
                .body("idade", equalTo(38))
                .body("id", equalTo(1987))
                .body("risco", equalTo(0));

        //limpardados
        apagarTodosClientes();
    }

    @Test
    @DisplayName("Quando apagar um cliente. Então seus dados devem ser removidos com sucesso.")
    public void deletarClientePorID(){

        Cliente clienteParaCadastrar = new Cliente ();

        clienteParaCadastrar.setNome("Maggie Simpson");
        clienteParaCadastrar.setIdade(1);
        clienteParaCadastrar.setId(1987);

        //criar cadastro
        given()
                .contentType(ContentType.JSON)
                .body(clienteParaCadastrar)
        .when()
                .post(servicoCliente + endpointCliente);

        //apagar cliente
        given()
                .contentType(ContentType.JSON)

        .when()
                .delete(servicoCliente + endpointCliente+"/"+ clienteParaCadastrar.getId())
        .then()
                .statusCode(200)
               .body(equalTo("CLIENTE REMOVIDO: { NOME: Maggie Simpson, IDADE: 1, ID: 1987 }"))
               .assertThat().body(not(contains("Maggie Simpson")));
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