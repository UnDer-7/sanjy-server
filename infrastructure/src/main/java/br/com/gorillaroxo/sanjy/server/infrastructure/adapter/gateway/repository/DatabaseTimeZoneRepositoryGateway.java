package br.com.gorillaroxo.sanjy.server.infrastructure.adapter.gateway.repository;

import br.com.gorillaroxo.sanjy.server.core.ports.driven.DatabaseTimeZoneGateway;
import br.com.gorillaroxo.sanjy.server.infrastructure.jpa.repository.GetDatabaseTimeZoneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DatabaseTimeZoneRepositoryGateway implements DatabaseTimeZoneGateway {

    private final GetDatabaseTimeZoneRepository repository;

    @Override
    public String get() {
        return repository.getDatabaseTimeZone();
    }

}
