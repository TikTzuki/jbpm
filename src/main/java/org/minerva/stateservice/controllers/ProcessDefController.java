package org.minerva.stateservice.controllers;

import org.jbpm.kie.services.impl.bpmn2.ProcessDescriptor;
import org.jbpm.services.api.DefinitionService;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.model.ProcessDefinition;
import org.kie.api.runtime.query.QueryContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

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
        ProcessDefinition definition = definitionService.getProcessDefinition(deployment, id);
        return definition;
    }


    @PostMapping(value = "/new")
    public Long newProcessDefinition(@RequestParam String deploymentId, @RequestParam String processId, @RequestBody ProcessDescriptor processDescriptor) {
        definitionService.addProcessDefinition(deploymentId, processId, processDescriptor, null);
        return null;
    }
}

