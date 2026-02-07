package br.com.gorillaroxo.sanjy.server.entrypoint.rest;

import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.ErrorResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.ProjectInfoResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.util.OpenApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Maintenance", description = "Handles maintenance")
public interface MaintenanceRestService {

    @Operation(summary = "Get project information", description = """
                Retrieves project information including version details (current and latest), \
                application and database timezone configuration, and the current runtime mode.
                """)
    @ApiResponse(
            responseCode = OpenApiConstants.HttpStatusCodes.OK,
            description = "Project information",
            content = @Content(schema = @Schema(implementation = ProjectInfoResponseDto.class)))
    @ApiResponse(
            responseCode = OpenApiConstants.HttpStatusCodes.INTERNAL_SERVER_ERROR,
            description = "unexpected error occurred",
            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    ProjectInfoResponseDto projectInfo();
}
