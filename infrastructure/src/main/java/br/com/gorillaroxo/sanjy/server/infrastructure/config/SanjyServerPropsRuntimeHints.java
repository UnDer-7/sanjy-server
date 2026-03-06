package br.com.gorillaroxo.sanjy.server.infrastructure.config;

import br.com.gorillaroxo.sanjy.server.infrastructure.jpa.config.MetadataEntityListener;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

public class SanjyServerPropsRuntimeHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(final RuntimeHints hints, final ClassLoader classLoader) {
        hints.reflection()
                .registerType(MetadataEntityListener.class, MemberCategory.values())
                .registerType(SanjyServerPropsConfig.class, MemberCategory.values())
                .registerType(SanjyServerPropsConfig.LoggingPropImpl.class, MemberCategory.values())
                .registerType(SanjyServerPropsConfig.ApplicationPropImpl.class, MemberCategory.values())
                .registerType(SanjyServerPropsConfig.ApplicationContactPropImpl.class, MemberCategory.values())
                .registerType(SanjyServerPropsConfig.ApplicationDocumentationPropImpl.class, MemberCategory.values())
                .registerType(SanjyServerPropsConfig.ExternalHttpClientsPropImpl.class, MemberCategory.values())
                .registerType(SanjyServerPropsConfig.GenericHttpClientsPropImpl.class, MemberCategory.values())
                .registerType(
                        SanjyServerPropsConfig.ExternalHttpClientsRetryConfigPropImpl.class, MemberCategory.values());
    }
}
