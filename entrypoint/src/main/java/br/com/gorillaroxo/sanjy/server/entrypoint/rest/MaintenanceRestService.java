package br.com.gorillaroxo.sanjy.server.entrypoint.rest;

import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.ProjectInfoResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Maintenance", description = "")
public interface MaintenanceRestService {

    ProjectInfoResponseDto projectInfo();

}
