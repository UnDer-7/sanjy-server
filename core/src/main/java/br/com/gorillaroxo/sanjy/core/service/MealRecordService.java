package br.com.gorillaroxo.sanjy.core.service;

import br.com.gorillaroxo.sanjy.core.domain.MealRecordDomain;
import br.com.gorillaroxo.sanjy.core.ports.driven.MealRecordGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class MealRecordService {

    private final MealRecordGateway mealRecordGateway;

    public MealRecordDomain insert(final MealRecordDomain mealRecordDomain) {
        mealRecordDomain.setConsumedAt(LocalDateTime.now());
        // todo: validar dados

        return mealRecordGateway.insert(mealRecordDomain);
    }
}
