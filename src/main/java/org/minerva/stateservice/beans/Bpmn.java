package org.minerva.stateservice.beans;

import org.jbpm.kie.services.impl.KModuleDeploymentService;
import org.jbpm.kie.services.impl.RuntimeDataServiceImpl;
import org.jbpm.kie.services.impl.bpmn2.BPMN2DataServiceImpl;
import org.jbpm.services.api.DefinitionService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.task.audit.JPATaskLifeCycleEventListener;
import org.jbpm.services.task.lifecycle.listeners.TaskLifeCycleEventListener;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.task.TaskService;
import org.kie.api.task.UserGroupCallback;
import org.kie.internal.identity.IdentityProvider;
import org.kie.spring.factorybeans.TaskServiceFactoryBean;
import org.kie.spring.manager.SpringRuntimeManagerFactoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class Bpmn {
    @Bean
    @DependsOn("bitronixTransactionManager,datasource")
    public PlatformTransactionManager transactionManager() {
        return new JtaTransactionManager();
    }

    @Bean
    @DependsOn("transactionManager")
    LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
        bean.setPersistenceXmlLocation("classpath:/META-INF/jbpmn-persistence.xml");
        return bean;
    }

    @Bean
    RuntimeManagerFactory runtimeManagerFactory() {
        return new SpringRuntimeManagerFactoryImpl() {
            @Autowired
            AbstractPlatformTransactionManager transactionManager;
            @Autowired
            UserGroupCallback userGroupCallback;

        };
    }

    @Bean
    DefinitionService definitionService() {
        return new BPMN2DataServiceImpl();
    }

    @Bean(destroyMethod = "close")
    public TaskServiceFactoryBean taskService() {
        TaskServiceFactoryBean bean = new TaskServiceFactoryBean() {
            @Autowired
            EntityManagerFactory entityManagerFactory;
            @Autowired
            PlatformTransactionManager transactionManager;
            @Autowired
            UserGroupCallback userGroupCallback;
        };

        TaskLifeCycleEventListener taskLifeCycleEventListener = new JPATaskLifeCycleEventListener(true);
        List<TaskLifeCycleEventListener> list = new ArrayList<>();
        list.add(taskLifeCycleEventListener);
        bean.setListeners(list);

        return bean;
    }

    @Bean
    public TransactionalCommandService transactionCmdService(EntityManagerFactory entityManagerFactory) {
        return new TransactionalCommandService(entityManagerFactory);
    }

    @Bean
    public RuntimeDataService runtimeDataService(
            @Autowired TransactionalCommandService transactionalCommandService,
            @Autowired IdentityProvider identityProviderr,
            @Autowired TaskServiceFactoryBean taskServiceFactoryBean
    ) throws Exception {
        RuntimeDataServiceImpl bean = new RuntimeDataServiceImpl();
        bean.setCommandService(transactionalCommandService);
        bean.setIdentityProvider(identityProviderr);
        bean.setTaskService((TaskService) taskServiceFactoryBean.getObject());
        return bean;
    }

    @Bean(initMethod = "onInit")
    public KModuleDeploymentService deploymentService(
            DefinitionService definitionService,
            EntityManagerFactory entityManagerFactory,
            RuntimeManagerFactory runtimeManagerFactory,
            IdentityProvider identityProvider,
            RuntimeDataService runtimeDataService
    ) {
        KModuleDeploymentService bean = new KModuleDeploymentService();
        bean.setBpmn2Service(definitionService);
        bean.setEmf(entityManagerFactory);
        bean.setManagerFactory(runtimeManagerFactory);
        bean.setIdentityProvider(identityProvider);
        bean.setRuntimeDataService(runtimeDataService);
        return bean;
    }
}
