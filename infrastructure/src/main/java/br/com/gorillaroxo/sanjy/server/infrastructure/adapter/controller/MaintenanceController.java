package br.com.gorillaroxo.sanjy.server.infrastructure.adapter.controller;

import br.com.gorillaroxo.sanjy.server.core.ProjectInfoDomain;
import br.com.gorillaroxo.sanjy.server.core.ports.driver.ProjectInfoUseCase;
import br.com.gorillaroxo.sanjy.server.entrypoint.dto.respose.ProjectInfoResponseDto;
import br.com.gorillaroxo.sanjy.server.entrypoint.rest.MaintenanceRestService;
import br.com.gorillaroxo.sanjy.server.infrastructure.config.McpToolMarker;
import br.com.gorillaroxo.sanjy.server.infrastructure.mapper.ProjectInfoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class MaintenanceController implements MaintenanceRestService, McpToolMarker {

    private final ProjectInfoUseCase projectInfoUseCase;
    private final ProjectInfoMapper projectInfoMapper;

    @Override
    @GetMapping(value = "/v1/project-info", produces = MediaType.APPLICATION_JSON_VALUE)
    @Tool(name = "projectInfo", description = "")
    public ProjectInfoResponseDto projectInfo() {
        final ProjectInfoDomain projectInfo = projectInfoUseCase.execute();
        return projectInfoMapper.toDto(projectInfo);
    }

}
