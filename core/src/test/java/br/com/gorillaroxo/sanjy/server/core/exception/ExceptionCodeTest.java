package br.com.gorillaroxo.sanjy.server.core.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ExceptionCodeTest {

    @Test
    void shouldNotHaveDuplicateCodes() {
        // Given
        ExceptionCode[] allExceptionCodes = ExceptionCode.values();

        // When
        Map<String, List<ExceptionCode>> codesByValue = Arrays.stream(allExceptionCodes)
                .collect(Collectors.groupingBy(ExceptionCode::getCode));

        List<String> duplicateCodes = codesByValue.entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1)
                .map(Map.Entry::getKey)
                .toList();

        // Then
        assertThat(duplicateCodes)
                .as("Found duplicate exception codes: %s in enum %s", duplicateCodes, ExceptionCode.class.getName())
                .isEmpty();
    }

}
