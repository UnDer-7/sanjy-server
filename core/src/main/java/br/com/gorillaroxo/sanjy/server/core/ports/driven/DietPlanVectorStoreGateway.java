package br.com.gorillaroxo.sanjy.server.core.ports.driven;

public interface DietPlanVectorStoreGateway {

    void upsertPlan(String planText, Long planId);

}
