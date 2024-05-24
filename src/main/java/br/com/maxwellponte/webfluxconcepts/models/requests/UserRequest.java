package br.com.maxwellponte.webfluxconcepts.models.requests;

public record UserRequest (
    String name,
    String email,
    String password
) { }
