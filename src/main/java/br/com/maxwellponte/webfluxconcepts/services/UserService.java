package br.com.maxwellponte.webfluxconcepts.services;

import br.com.maxwellponte.webfluxconcepts.entities.User;
import br.com.maxwellponte.webfluxconcepts.mappers.UserMapper;
import br.com.maxwellponte.webfluxconcepts.models.requests.UserRequest;
import br.com.maxwellponte.webfluxconcepts.repositories.UserRepository;
import br.com.maxwellponte.webfluxconcepts.services.exceptions.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    public Mono<User> save(final UserRequest request){
        return repository.save(mapper.toEntity(request));
    }

    public Mono<User> findById(final String id) {
        return handleNotFound(repository.findById(id), id);
    }

    public Flux<User> findAll() {
        return repository.findAll();
    }

    public Mono<User> update(final String id, final UserRequest request) {
        return findById(id)
                .map(entity -> mapper.toEntity(request, entity))
                .flatMap(repository::save);

    }

    public Mono<User> delete(final String id){
        return handleNotFound(repository.findAndRemove(id), id);
    }

    private <T> Mono<T> handleNotFound(Mono<T> mono, String id){
        return mono .switchIfEmpty(Mono.error(
                new ObjectNotFoundException(String.format("Object not Found. Id: %s, type: %s", id, User.class.getSimpleName()))
        ));
    }
}
