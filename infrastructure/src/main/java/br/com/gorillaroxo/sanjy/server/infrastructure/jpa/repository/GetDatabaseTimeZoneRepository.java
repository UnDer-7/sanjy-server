package br.com.gorillaroxo.sanjy.server.infrastructure.jpa.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GetDatabaseTimeZoneRepository {

    private final EntityManager entityManager;

    public String getDatabaseTimeZone() {
        return (String) entityManager.createNativeQuery("show timezone").getSingleResult();
    }
}
