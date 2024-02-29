package com.groupeisi.springgraphQL.controller;

import com.groupeisi.springgraphQL.dao.EtudiantRepository;
import com.groupeisi.springgraphQL.entity.Etudiant;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.GraphQL;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import graphql.ExecutionResult;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/etudiants")
public class EtudiantController {

    private EtudiantRepository etudiantRepository;

    @Value("classpath:etudiant.graphqls")
    private Resource schemaResource;

    private GraphQL graphQL;

    public EtudiantController(EtudiantRepository etudiantRepository) {
        this.etudiantRepository = etudiantRepository;
    }

    @PostConstruct
    public void loadSchema() throws IOException {
        File schemaFile = schemaResource.getFile();
        TypeDefinitionRegistry registry = new SchemaParser().parse(schemaFile);
        RuntimeWiring wiring = buildWiring();
        GraphQLSchema schema = new SchemaGenerator().makeExecutableSchema(registry, wiring);
        graphQL = GraphQL.newGraphQL(schema).build();
    }

    private RuntimeWiring buildWiring() {
        DataFetcher<List<Etudiant>> fetcher1 = data -> {
            return (List<Etudiant>) etudiantRepository.findAll();
        };

        DataFetcher<Etudiant> fetcher2 = data -> {
            return etudiantRepository.findByEmail(data.getArgument("email"));
        };

        return RuntimeWiring.newRuntimeWiring().type("Query",
                        typeWriting -> typeWriting.dataFetcher("getAllEtudiant", fetcher1).dataFetcher("findEtudiant", fetcher2))
                .build();

    }

    @PostMapping("/getAllEtudiant")
    public ResponseEntity<Object> getAllEtudiant(@RequestBody String query) {
        ExecutionResult result = graphQL.execute(query);
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }

    @PostMapping("/getEtudiantByEmail")
    public ResponseEntity<Object> getEtudiantByEmail(@RequestBody String query) {
        ExecutionResult result = graphQL.execute(query);
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }

    @GetMapping
    public List<Etudiant> getAll(){
        return etudiantRepository.findAll();
    }
    @GetMapping("/{id}")
    public Etudiant getById(@PathVariable("id") int id){
        return etudiantRepository.findById(id).orElseThrow();
    }
}
