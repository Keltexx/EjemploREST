package services;

import data.People;
import data.Person;
import data.PostalAddress;
import es.uji.www.GeneradorDatosINE;
import store.PersonDAO;
import store.PersonStorage;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("people")
public class PersonService {
    private GeneradorDatosINE generador = new GeneradorDatosINE();
    @Inject
    private PersonStorage storage;

    @GET
    @Produces({"application/xml", "application/json"})
    public Response retrieveAll() {
        return Response
                .status(Response.Status.OK)
                .entity(storage.retrieveAll())
                .build();
    }

    @POST
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    public Response create(Person person) {
        Person retrievedPerson = storage.retrieve(person.getNif());
        if(retrievedPerson == PersonDAO.NOT_FOUND) {
            storage.create(person);
            Person found = storage.retrieve(person.getNif());
            return Response
                    .status(Response.Status.CREATED)
                    .entity(found)
                    .build();
        } else {
            return Response
                    .status(Response.Status.CONFLICT)
                    .build();
        }
    }

    @GET
    @Produces({"application/xml", "application/json"})
    @Path("{nif}")
    public Response retrieve(@PathParam("nif") String nif) {
        Person person = storage.retrieve(nif);
        if (person != PersonDAO.NOT_FOUND)
            return Response
                .status(Response.Status.OK)
                .entity(person)
                .build();
        else return Response
                .status(Response.Status.NOT_FOUND)
                .build();
    }

    @PUT
    @Produces({"application/xml", "application/json"})
    @Path("{nif}")
    public Response update(@PathParam("nif") String nif, Person person) {
        Person retrievePerson = storage.retrieve(nif);
        if(retrievePerson != PersonDAO.NOT_FOUND) {
            storage.update(person);
            return Response
                    .status(Response.Status.NO_CONTENT)
                    .build();
        } else return Response
                    .status(Response.Status.NOT_FOUND)
                    .build();
    }

    @DELETE
    @Path("{nif}")
    public Response delete(@PathParam("nif") String nif) {
        Person person = storage.retrieve(nif);
        if(person != PersonDAO.NOT_FOUND) {
            storage.delete(nif);
            return Response
                    .status(Response.Status.NO_CONTENT)
                    .build();
        } else return Response
                .status(Response.Status.NOT_FOUND)
                .build();
    }

//    Este es un método de ayuda, no forma parte del API
    @POST
    @Path("generate/random")
    @Produces({"application/xml", "application/json"})
    public Response generateRandom(@QueryParam("quantity") int quantity) {
        String name, surname, province;
        PostalAddress address;
        Person person;
        for(int i = 0; i < quantity; i++) {
            province = generador.getProvincia();
            address = new PostalAddress(generador.getPoblacion(province), province);
            person = new Person(generador.getNombre(), generador.getApellido(), generador.getNIF(), address);
            storage.create(person);
        }
        return Response
                .status(Response.Status.OK)
                .build();
    }
}
