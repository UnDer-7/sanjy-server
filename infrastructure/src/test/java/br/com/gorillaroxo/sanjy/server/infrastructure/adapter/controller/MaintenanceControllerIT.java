package br.com.gorillaroxo.sanjy.server.infrastructure.adapter.controller;

import br.com.gorillaroxo.sanjy.server.infrastructure.test.IntegrationTestController;

class MaintenanceControllerIT extends IntegrationTestController {

    @Override
    protected String getBaseUrl() {
        return "/v1/maintenance";
    }

}