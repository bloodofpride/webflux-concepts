package br.com.maxwellponte.webfluxconcepts.services.exceptions;

public class ObjectNotFoundException extends RuntimeException{
    public ObjectNotFoundException(String msg){
        super(msg);
    }
}
