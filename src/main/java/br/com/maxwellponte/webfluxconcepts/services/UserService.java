package br.com.maxwellponte.webfluxconcepts.services;

import br.com.maxwellponte.webfluxconcepts.entities.User;
import br.com.maxwellponte.webfluxconcepts.mappers.UserMapper;
import br.com.maxwellponte.webfluxconcepts.models.requests.UserRequest;
import br.com.maxwellponte.webfluxconcepts.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
        return repository.findById(id);
    }
}
