package br.com.maxwellponte.webfluxconcepts.mappers;
import br.com.maxwellponte.webfluxconcepts.entities.User;
import br.com.maxwellponte.webfluxconcepts.models.requests.UserRequest;
import br.com.maxwellponte.webfluxconcepts.models.responses.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import reactor.core.publisher.Mono;

import static org.mapstruct.NullValueCheckStrategy.ALWAYS;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = IGNORE,
        nullValueCheckStrategy = ALWAYS
)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    User toEntity(final UserRequest request);

    UserResponse toResponse(final User user);
}
