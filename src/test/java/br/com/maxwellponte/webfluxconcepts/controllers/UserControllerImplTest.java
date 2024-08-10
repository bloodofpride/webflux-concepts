package br.com.maxwellponte.webfluxconcepts.controllers;

import br.com.maxwellponte.webfluxconcepts.entities.User;
import br.com.maxwellponte.webfluxconcepts.mappers.UserMapper;
import br.com.maxwellponte.webfluxconcepts.models.requests.UserRequest;
import br.com.maxwellponte.webfluxconcepts.models.responses.UserResponse;
import br.com.maxwellponte.webfluxconcepts.services.UserService;
import br.com.maxwellponte.webfluxconcepts.services.exceptions.ObjectNotFoundException;
import com.mongodb.reactivestreams.client.MongoClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
class UserControllerImplTest {

    public static final String ID = "123456";
    public static final String NAME = "Maxwell";
    public static final String EMAIL = "maxwell@mail.com";
    public static final String PASSWORD = "123";
    public static final String BASE_URI = "/users";
    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserService userService;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private MongoClient mongoClient;

    @Test
    @DisplayName("Test endpoint save with success")
    void testSaveWithSuccess() {
        UserRequest userRequest = new UserRequest(NAME, EMAIL, PASSWORD);

        when(userService.save(any(UserRequest.class))).thenReturn(Mono.just(User.builder().build()));

        webTestClient.post().uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userRequest))
                .exchange()
                .expectStatus().isCreated();

        Mockito.verify(userService, times(1)).save(any(UserRequest.class));
    }

    @Test
    @DisplayName("Test endpoint save with bad request")
    void testSaveWithBadRequest() {
        UserRequest userRequest = new UserRequest(NAME.concat(" "), EMAIL, PASSWORD);

        webTestClient.post().uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userRequest))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.path").isEqualTo(BASE_URI)
                .jsonPath("$.status").isEqualTo(HttpStatus.BAD_REQUEST.value())
                .jsonPath("$.error").isEqualTo("Validation Errors")
                .jsonPath("$.message").isEqualTo("Error on validation attributes")
                .jsonPath("$.errors[0].fieldName").isEqualTo("name")
                .jsonPath("$.errors[0].message").isEqualTo("field cannot have blank spaces at the beginning or at end");

        Mockito.verify(userService, times(0)).save(any(UserRequest.class));
    }

    @Test
    @DisplayName("Test endpoint findById with success")
    void testFindByIdWithSuccess() {
        UserResponse userResponse = new UserResponse(ID, NAME, EMAIL);

        when(userService.findById(anyString())).thenReturn(Mono.just(User.builder().build()));
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);

        webTestClient.get().uri(BASE_URI + "/" + ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(ID)
                .jsonPath("$.name").isEqualTo(NAME)
                .jsonPath("$.email").isEqualTo(EMAIL);
    }

    @Test
    @DisplayName("Test endpoint findById with not found")
    void testFindByIdWithNotFound() {
        when(userService.findById(anyString())).thenThrow(new ObjectNotFoundException(String.format("Object not Found. Id: %s, type: %s", ID, User.class.getSimpleName())));

        webTestClient.get().uri(BASE_URI + "/" + ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.path").isEqualTo(BASE_URI + "/" +ID)
                .jsonPath("$.status").isEqualTo(HttpStatus.NOT_FOUND.value())
                .jsonPath("$.error").isEqualTo("Not Found")
                .jsonPath("$.message").isEqualTo(String.format("Object not Found. Id: %s, type: %s", ID, User.class.getSimpleName()));
    }

    @Test
    @DisplayName("Test endpoint findAll with success")
    void testFindAllWithSuccess() {
        UserResponse userResponse = new UserResponse(ID, NAME, EMAIL);

        when(userService.findAll()).thenReturn(Flux.just(User.builder().build()));
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);

        webTestClient.get().uri(BASE_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.[0].id").isEqualTo(ID)
                .jsonPath("$.[0].name").isEqualTo(NAME)
                .jsonPath("$.[0].email").isEqualTo(EMAIL);
    }

    @Test
    @DisplayName("Test endpoint update with success")
    void testUpdateWithSuccess() {
        UserRequest userRequest = new UserRequest(NAME, EMAIL, PASSWORD);
        UserResponse userResponse = new UserResponse(ID, NAME, EMAIL);

        when(userService.update(anyString(), any(UserRequest.class))).thenReturn(Mono.just(User.builder().build()));
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);

        webTestClient.patch().uri(BASE_URI + "/" + ID)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userRequest))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(ID)
                .jsonPath("$.name").isEqualTo(NAME)
                .jsonPath("$.email").isEqualTo(EMAIL);

        Mockito.verify(userService, times(1)).update(anyString(), any(UserRequest.class));
        Mockito.verify(userMapper, times(1)).toResponse(any(User.class));

    }

    @Test
    @DisplayName("Test endpoint delete with success")
    void testDeleteWithSuccess() {
        when(userService.delete(anyString())).thenReturn(Mono.just(User.builder().build()));

        webTestClient.delete().uri(BASE_URI + "/" + ID)
                .exchange()
                .expectStatus().isOk();

        Mockito.verify(userService, times(1)).delete(anyString());
    }

    @Test
    @DisplayName("Test endpoint delete with not found")
    void testDeleteWithNotFound() {
        when(userService.delete(anyString())).thenThrow(new ObjectNotFoundException(String.format("Object not Found. Id: %s, type: %s", ID, User.class.getSimpleName())));

        webTestClient.delete().uri(BASE_URI + "/" + ID)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.path").isEqualTo(BASE_URI + "/" +ID)
                .jsonPath("$.status").isEqualTo(HttpStatus.NOT_FOUND.value())
                .jsonPath("$.error").isEqualTo("Not Found")
                .jsonPath("$.message").isEqualTo(String.format("Object not Found. Id: %s, type: %s", ID, User.class.getSimpleName()));

    }
}