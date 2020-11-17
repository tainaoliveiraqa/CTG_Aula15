import io.restassured.http.ContentType;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class TesteApiCliente {

    //URIs utilizadas.
    String servicoCliente = "http://localhost:8080";
    String endpointCliente = "/cliente";

    //Dados para cadastro
    String clienteParaCadastrar = " { \n" +
            "    \"nome\": \"Taina Mendes\",\n" +
            "    \"idade\": 33, \n" +
            "    \"id\": \"1987\" \n" +
            "}";

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
                .body(new IsEqual("{}"));

    }

    @Test
    @DisplayName("Quando cadastrar um cliente. Então na resposta deve retornar os dados do cliente cadastrado.")
    public void cadastraCliente() {

        //POST Cadastrar um cliente
        given()
                .contentType(ContentType.JSON)
                .body(clienteParaCadastrar)
                .when()
                .post(servicoCliente + endpointCliente)
                .then()
                .statusCode(201)
                .body("1987.nome", equalTo("Taina Mendes"))
                .body("1987.idade", equalTo(33))
                .body("1987.id", equalTo(1987))
                .body("1987.risco", equalTo(0));

        //limpardados
        apagarTodosClientes();

    }

    @Test
    @DisplayName("Quando atualizar os dados do cliente. Então na resposta deve retornar os dados do cliente atualizado.")
    public void atualizarDadosCliente(){

        String clienteParaAtualizar = " { \n" +
                "    \"nome\": \"Taina Oliveira\",\n" +
                "    \"idade\": 34, \n" +
                "    \"id\": \"1987\" \n" +
                "}";

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
                .body("1987.nome", equalTo("Taina Oliveira"))
                .body("1987.idade", equalTo(34));

        //limpardados
        apagarTodosClientes();
    }

    @Test
    @DisplayName("Quando pesquisar um cliente por ID. Então na resposta deve retornar os dados desse cliente específico.")
    public void consultarClientePorID(){

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
                .get(servicoCliente + endpointCliente +"/1987")
                .then()
                .statusCode(200)
                .body("nome", equalTo("Taina Mendes"))
                .body("idade", equalTo(33))
                .body("id", equalTo(1987))
                .body("risco", equalTo(0));
    }

    @Test
    @DisplayName("Quando apagar um cliente. Então seus dados devem ser removidos com sucesso.")
    public void deletarClientePorID(){

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
                .delete(servicoCliente + endpointCliente+"/1987")
                .then()
                .statusCode(200)
                .body(equalTo("CLIENTE REMOVIDO: { NOME: Taina Mendes, IDADE: 33, ID: 1987 }"));
    }


    @Test
    @DisplayName("Quando apagar todos os clientes. Então na resposta não retornar nenhum cliente.")
    public void apagarTodosClientes() {
        String endpointApagarTodos = "/cliente/apagaTodos";

        //deletar dados
        given()
                .contentType(ContentType.JSON)
                .when()
                .delete(servicoCliente + endpointApagarTodos)
                .then()
                .statusCode(200)
                .body(new IsEqual("{}"));
    }

}
