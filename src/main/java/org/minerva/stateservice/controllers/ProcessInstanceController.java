package org.minerva.stateservice.controllers;


import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.kie.api.runtime.query.QueryContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/process-instance")
public class ProcessInstanceController {
    @Autowired
    private RuntimeDataService runtimeDataService;

    @Autowired
    private ProcessService processService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public Collection<ProcessInstanceDesc> getProcessInstances() {
        return runtimeDataService.getProcessInstances(new QueryContext(0, 100, "status", true));
    }

    @GetMapping(value = "/show")
    public ProcessInstanceDesc getProcessInstance(@RequestParam String id) {
        long processInstanceId = Long.parseLong(id);
        return runtimeDataService.getProcessInstanceById(processInstanceId);
    }

    @PostMapping(value = "/abort")
    public String abortProcessInstance(@RequestParam String id) {
        processService.abortProcessInstance(Long.parseLong(id));
        return "Instance (" + id + ") aborted successfully";
    }

    @PostMapping(value = "/signal")
    public String signalProcessInstance(@RequestParam String id, @RequestParam String signal, @RequestParam String data) {
        processService.signalProcessInstance(Long.parseLong(id), signal, data);
        return "Signal sent to instance (" + id + ") successfully";
    }

    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public Long newProcessInstance(
            @RequestParam String deploymentId, @RequestParam String processId,
            @RequestParam Map<String, String> allRequestParams
    ) {
        return processService.startProcess(deploymentId, processId, new HashMap<>(allRequestParams));
    }
}
