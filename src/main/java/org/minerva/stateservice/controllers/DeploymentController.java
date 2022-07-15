package org.minerva.stateservice.controllers;

import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.model.DeployedUnit;
import org.jbpm.services.api.model.DeploymentUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/deployment")
public class DeploymentController {
    @Autowired
    DeploymentService deploymentService;

    @PostMapping("/")
    public DeployedUnit deploy() {
        String GROUP_ID = "org.minerva";
        String ARTIFACT_ID = "TIK";
        String VERSION = "0.0.1";
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnit);
        return deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
    }
}
