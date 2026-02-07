package br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Response DTO containing project information such as version, timezone, and runtime mode")
public record ProjectInfoResponseDto(
        @Schema(description = "Version information of the project", requiredMode = Schema.RequiredMode.REQUIRED)
        Version version,

        @Schema(
                description = "Timezone configuration for the application and database",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Timezone timezone,

        @Schema(
                description = "Current runtime mode of the application (JVM or Native)",
                example = "JVM",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String runtimeMode) {

    @Builder
    @Schema(description = "Version details including current version, latest available version, and whether they match")
    public record Version(
            @Schema(
                    description = "Current version of the application",
                    example = "1.0.0",
                    requiredMode = Schema.RequiredMode.REQUIRED)
            String current,

            @Schema(
                    description = "Latest available version of the application",
                    example = "1.0.0",
                    nullable = true,
                    requiredMode = Schema.RequiredMode.NOT_REQUIRED)
            String latest,

            @Schema(
                    description = "Indicates whether the current version is the latest",
                    example = "true",
                    requiredMode = Schema.RequiredMode.REQUIRED)
            Boolean isLatest) {}

    @Builder
    @Schema(description = "Timezone configuration details for the application and database layers")
    public record Timezone(
            @Schema(
                    description = "Timezone configured for the application",
                    example = "UTC",
                    requiredMode = Schema.RequiredMode.REQUIRED)
            String application,

            @Schema(
                    description = "Timezone configured for the database",
                    example = "UTC",
                    nullable = true,
                    requiredMode = Schema.RequiredMode.NOT_REQUIRED)
            String database) {}
}
