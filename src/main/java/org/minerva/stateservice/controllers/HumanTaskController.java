package org.minerva.stateservice.controllers;

import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.UserTaskService;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.query.QueryFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@SpringBootApplication
//@RestController
//public class HumanTaskController {
//    @Autowired
//    private ProcessService processService;
//
//    @Autowired
//    private RuntimeDataService runtimeDataService;
//    @Autowired
//    private UserTaskService userTaskService;
//
//    @GetMapping("/hello")
//    public ResponseEntity<String> sayHello(@RequestParam Integer age) {
//        Map<String, Object> vars = new HashMap<>();
//        vars.put("processVar1", "hello");
//        Long processInstanceId = processService.startProcess("simpleProcess", "org.minerva.simpleProcess", vars);
//        Map<String, Object> params = new HashMap<>();
//        params.put("age", age);
//
//        List<TaskSummary> taskSummaries = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
//
//        taskSummaries.forEach(s -> {
//            Status status = taskSummaries.get(0).getStatus();
//            if (status == Status.Ready)
//                userTaskService.claim(s.getId(), "john");
//            userTaskService.start(s.getId(), "john");
//            userTaskService.complete(s.getId(), "john", params);
//        });
//        return ResponseEntity.status(HttpStatus.CREATED).body("Task completed!");
//    }
//
//    public static void main(String[] args) {
//        SpringApplication.run(HumanTaskController.class, args);
//    }
//}
