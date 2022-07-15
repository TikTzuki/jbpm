package org.minerva.stateservice.services;

import bitronix.tm.BitronixTransactionManager;
import org.jbpm.kie.services.impl.ProcessServiceImpl;
import org.jbpm.services.api.DeploymentService;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.runtime.manager.InternalRuntimeManager;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.jta.JtaTransactionManager;

import java.util.Map;

@Service
public class ProcessService extends ProcessServiceImpl {
    @Autowired
    DeploymentService deploymentService;
    @Autowired
    JtaTransactionManager jtaTransactionManager;
    @Autowired
    BitronixTransactionManager bitronixTransactionManager;

    public Long startProcess(String deploymentId, String processId, Map<String, Object> params) {
        RuntimeManager manager;
        params = this.process(params, ((InternalRuntimeManager) manager).getEnvironment().getClassLoader());
        RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession kSession = engine.getKieSession();
        ProcessInstance pi = null;
        Long var9;
        try {
            pi = kSession.startProcess(processId, params);
            var9 = pi.getId();
        } finally {
            this.disposeRuntimeEngine(manager, engine);
        }

        return var9;
    }
}
