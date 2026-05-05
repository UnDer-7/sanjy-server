package br.com.gorillaroxo.sanjy.server.core.usecase;

import br.com.gorillaroxo.sanjy.server.core.domain.LogField;
import br.com.gorillaroxo.sanjy.server.core.domain.PatchableStandardOptionDomain;
import br.com.gorillaroxo.sanjy.server.core.domain.StandardOptionDomain;
import br.com.gorillaroxo.sanjy.server.core.exception.StandardOptionNotFoundException;
import br.com.gorillaroxo.sanjy.server.core.ports.driven.StandardOptionGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PatchStandardOptionUseCase {

    private final StandardOptionGateway standardOptionGateway;

    public StandardOptionDomain execute(final PatchableStandardOptionDomain patchable) {
        final StandardOptionDomain stdOption = standardOptionGateway
                .findById(patchable.getId())
                .orElseThrow(() -> new StandardOptionNotFoundException("Could not find standard option with id " + patchable.getId()));

        log.info(
                LogField.Placeholders.TWO.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "Standard Option before patching"),
                StructuredArguments.kv(LogField.STANDARD_OPTION_ID.label(), stdOption.id()));

        final StandardOptionDomain patched = stdOption.patch(patchable);
        final StandardOptionDomain saved = standardOptionGateway.patch(patched);

        log.info(
                LogField.Placeholders.TWO.getPlaceholder(),
                StructuredArguments.kv(LogField.MSG.label(), "Standard Option after patching"),
                StructuredArguments.kv(LogField.STANDARD_OPTION_ID.label(), saved.id()));

        return saved;
    }
}
