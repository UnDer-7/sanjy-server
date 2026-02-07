package br.com.gorillaroxo.sanjy.server.infrastructure.adapter.gateway.lib;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SemanticVersioningComparatorLibGatewayTest {

    @InjectMocks
    SemanticVersioningComparatorLibGateway gateway;

    @ParameterizedTest
    @MethodSource("currentVersionIsLowerThanTarget")
    void shouldReturnNegativeWhenCurrentVersionIsLowerThanTarget(String current, String target) {
        assertThat(gateway.compare(current, target)).isNegative();
    }

    @ParameterizedTest
    @MethodSource("versionsAreEqual")
    void shouldReturnZeroWhenVersionsAreEqual(String current, String target) {
        assertThat(gateway.compare(current, target)).isZero();
    }

    @ParameterizedTest
    @MethodSource("currentVersionIsGreaterThanTarget")
    void shouldReturnPositiveWhenCurrentVersionIsGreaterThanTarget(String current, String target) {
        assertThat(gateway.compare(current, target)).isPositive();
    }

    static Stream<Arguments> currentVersionIsLowerThanTarget() {
        return Stream.of(
                // patch bump
                Arguments.of("1.0.0", "1.0.1"),
                // minor bump
                Arguments.of("1.0.0", "1.1.0"),
                // major bump
                Arguments.of("1.0.0", "2.0.0"),
                // pre-release is lower than release
                Arguments.of("1.0.0-alpha", "1.0.0"),
                Arguments.of("1.0.0-beta", "1.0.0"),
                Arguments.of("1.0.0-rc.1", "1.0.0"),
                // alpha ordering of pre-release
                Arguments.of("1.0.0-alpha", "1.0.0-beta"),
                Arguments.of("1.0.0-alpha.1", "1.0.0-alpha.2"),
                Arguments.of("1.0.0-beta", "1.0.0-rc.1"),
                // zero-based versions
                Arguments.of("0.1.0", "0.2.0"),
                Arguments.of("0.0.1", "0.1.0"),
                // higher minor/patch segments
                Arguments.of("2.44.0", "2.45.0"),
                Arguments.of("4.4.3", "4.4.4"),
                // pre-release numbering
                Arguments.of("2.3.4-alpha.1", "2.3.4-alpha.2"),
                Arguments.of("1.0.0-alpha-1", "1.0.0-alpha-2"),
                // complex pre-release tags
                Arguments.of("3.0.0-beta.1", "3.0.0-beta.2"),
                Arguments.of("1.0.0-alpha", "1.0.0-alpha.1"),
                // large version numbers
                Arguments.of("10.20.30", "10.20.31"),
                Arguments.of("99.99.99", "100.0.0")
        );
    }

    static Stream<Arguments> versionsAreEqual() {
        return Stream.of(
                Arguments.of("1.0.0", "1.0.0"),
                Arguments.of("0.1.0", "0.1.0"),
                Arguments.of("2.44.0", "2.44.0"),
                Arguments.of("4.4.4-alpha", "4.4.4-alpha"),
                Arguments.of("2.3.4-alpha.1", "2.3.4-alpha.1"),
                Arguments.of("1.0.0-alpha-2", "1.0.0-alpha-2"),
                Arguments.of("10.20.30", "10.20.30"),
                Arguments.of("0.0.1", "0.0.1"),
                Arguments.of("3.0.0-rc.1", "3.0.0-rc.1")
        );
    }

    static Stream<Arguments> currentVersionIsGreaterThanTarget() {
        return Stream.of(
                // patch bump
                Arguments.of("1.0.1", "1.0.0"),
                // minor bump
                Arguments.of("1.1.0", "1.0.0"),
                // major bump
                Arguments.of("2.0.0", "1.0.0"),
                // release is greater than pre-release
                Arguments.of("1.0.0", "1.0.0-alpha"),
                Arguments.of("1.0.0", "1.0.0-beta"),
                Arguments.of("1.0.0", "1.0.0-rc.1"),
                // pre-release ordering
                Arguments.of("1.0.0-beta", "1.0.0-alpha"),
                Arguments.of("1.0.0-alpha.2", "1.0.0-alpha.1"),
                Arguments.of("1.0.0-rc.1", "1.0.0-beta"),
                // higher minor/patch segments
                Arguments.of("2.45.0", "2.44.0"),
                Arguments.of("4.4.4", "4.4.3"),
                // pre-release numbering
                Arguments.of("2.3.4-alpha.2", "2.3.4-alpha.1"),
                Arguments.of("1.0.0-alpha-2", "1.0.0-alpha-1"),
                // large version numbers
                Arguments.of("10.20.31", "10.20.30"),
                Arguments.of("100.0.0", "99.99.99")
        );
    }
}
