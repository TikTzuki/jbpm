package org.minerva.stateservice.controllers;

import org.jbpm.services.api.DefinitionService;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.model.ProcessDefinition;
import org.kie.api.runtime.query.QueryContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("process-def")
public class ProcessDefController {
    @Autowired
    private RuntimeDataService runtimeDataService;

    @Autowired
    private ProcessService processService;
    @Autowired
    private DefinitionService definitionService;

    @GetMapping(value = "/")
    public Collection<ProcessDefinition> getProcessDef() {
        return runtimeDataService.getProcesses(new QueryContext(0, 100));
    }

    @GetMapping(value = "/show")
    public ProcessDefinition getProcessDefinition(@RequestParam String deployment, @RequestParam String id) {
        return runtimeDataService.getProcessesByDeploymentIdProcessId(deployment, id);
    }


    @PostMapping(value = "/new")
    public Long newProcessInstance(@RequestParam String deploymentId, @RequestParam String processId, @RequestParam Map<String, String> allRequestParams) {
        long processInstanceId = processService.startProcess(deploymentId, processId, new HashMap<>(allRequestParams));
        return processInstanceId;
    }
}

