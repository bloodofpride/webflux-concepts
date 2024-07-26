package br.com.maxwellponte.webfluxconcepts.repositories;

import br.com.maxwellponte.webfluxconcepts.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    @Autowired
    private final ReactiveMongoTemplate template;

    public Mono<User> save(final User user) {
        return template.save(user);
    }

    public Mono<User> findById(String id) {
        return template.findById(id, User.class);
    }

    public Flux<User> findAll() {
        return template.findAll(User.class);
    }
}
