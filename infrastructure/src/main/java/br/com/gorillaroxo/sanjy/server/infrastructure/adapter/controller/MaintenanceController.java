package br.com.gorillaroxo.sanjy.server.infrastructure.adapter.controller;

import br.com.gorillaroxo.sanjy.server.core.ProjectInfoDomain;
import br.com.gorillaroxo.sanjy.server.core.ports.driver.ProjectInfoUseCase;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.ProjectInfoResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.rest.MaintenanceRestService;
import br.com.gorillaroxo.sanjy.server.infrastructure.adapter.controller.config.SanjyEndpoint;
import br.com.gorillaroxo.sanjy.server.infrastructure.config.McpToolMarker;
import br.com.gorillaroxo.sanjy.server.infrastructure.mapper.ProjectInfoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@SanjyEndpoint("/v1/maintenance")
@RequiredArgsConstructor
public class MaintenanceController implements MaintenanceRestService, McpToolMarker {

    private final ProjectInfoUseCase projectInfoUseCase;
    private final ProjectInfoMapper projectInfoMapper;

    @Override
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/project-info", produces = MediaType.APPLICATION_JSON_VALUE)
    @Tool(name = "projectInfo", description = """
            Retrieves project information including version details (current and latest), \
            application and database timezone configuration, and the current runtime mode.
            """)
    public ProjectInfoResponseDto projectInfo() {
        final ProjectInfoDomain projectInfo = projectInfoUseCase.execute();
        return projectInfoMapper.toDto(projectInfo);
    }
}
