package br.com.gorillaroxo.sanjy.core.ports.driven;

public interface DietPlanVectorStoreGateway {

    void upsertPlan(String planText, Long planId);

}
