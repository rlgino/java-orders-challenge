package com.rlgino.OrdersService;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

public class TeamViewerChallengeApplicationTest {

    @Test
    void createApplicationModuleModel() {
        ApplicationModules modules = ApplicationModules.of(TeamviewerChallengeApplication.class);
        modules.forEach(System.out::println);
    }

    @Test
    void verifiesModularStructure() {
        ApplicationModules modules = ApplicationModules.of(TeamviewerChallengeApplication.class);
        modules.verify();
    }

    @Test
    void createModuleDocumentation() {
        ApplicationModules modules = ApplicationModules.of(TeamviewerChallengeApplication.class);
        new Documenter(modules)
                .writeDocumentation()
                .writeIndividualModulesAsPlantUml();
    }
}
