package org.minerva.stateservice.beans;


import bitronix.tm.BitronixTransactionManager;
import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import bitronix.tm.resource.jdbc.lrc.LrcXADataSource;
import org.jbpm.kie.services.impl.KModuleDeploymentService;
import org.jbpm.kie.services.impl.RuntimeDataServiceImpl;
import org.jbpm.services.api.DefinitionService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.task.audit.JPATaskLifeCycleEventListener;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
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
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.UserTransaction;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Configuration
public class Bpmn {
    @Autowired
    public Bpmn(Environment environment) {
        String property = "org.jbpm.ht.callback";
        Properties properties = System.getProperties();
        properties.setProperty(property, environment.getProperty(property));
    }

    @Bean
    public bitronix.tm.Configuration btmConfig() {
        return TransactionManagerServices.getConfiguration();
    }

    @Bean(destroyMethod = "shutdown")
    @DependsOn("btmConfig")
    public BitronixTransactionManager bitronixTransactionManager() {
        return TransactionManagerServices.getTransactionManager();
    }

    @Bean(initMethod = "init", destroyMethod = "close")
    PoolingDataSource datasource() {
        PoolingDataSource bean = new PoolingDataSource();
        bean.setUniqueName("jdbc/jbpm");
        bean.setClassName(LrcXADataSource.class.getName());
        bean.setMaxPoolSize(20);
        bean.setAllowLocalTransactions(true);
        Properties properties = new Properties();
        properties.setProperty("user", "prometheus");
        properties.setProperty("password", "123456");
        properties.setProperty("driverClassName", "oracle.jdbc.OracleDriver");
        properties.setProperty("url", "jdbc:oracle:thin:@localhost:1521:xe");
        bean.setDriverProperties(properties);
        return bean;
    }

    @Bean
    @DependsOn({"bitronixTransactionManager", "datasource"})
    JtaTransactionManager transactionManager(UserTransaction userTransaction) {
        return new JtaTransactionManager(userTransaction);
    }

    @Bean
    public EntityManagerFactory entityManagerFactory(
    ) {
        return Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
    }

    @Bean
    public RuntimeManagerFactory runtimeManagerFactory(
            JtaTransactionManager transactionManager,
            UserGroupCallback userGroupCallback
    ) {
        SpringRuntimeManagerFactoryImpl bean = new SpringRuntimeManagerFactoryImpl();
        bean.setTransactionManager(transactionManager);
        bean.setUserGroupCallback(userGroupCallback);
        return bean;
    }

    @Bean
    UserGroupCallback userGroupCallback() throws Exception {
        Properties properties = new Properties();
        properties.load(new ClassPathResource("role.properties").getInputStream());
        return new JBossUserGroupCallbackImpl(properties);

    }


    @Bean(destroyMethod = "close")
    public TaskServiceFactoryBean taskService(
            JtaTransactionManager transactionManager,
            UserGroupCallback userGroupCallback,
            EntityManagerFactory emf
    ) {
        TaskServiceFactoryBean bean = new TaskServiceFactoryBean();
        bean.setTransactionManager(transactionManager);
        bean.setEntityManagerFactory(emf);

        bean.setUserGroupCallback(userGroupCallback);

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
            IdentityProvider identityProvider,
            TaskServiceFactoryBean taskServiceFactoryBean,
            TransactionalCommandService transactionCmdService
    ) throws Exception {

        RuntimeDataServiceImpl bean = new RuntimeDataServiceImpl();
        bean.setCommandService(transactionCmdService);
        bean.setIdentityProvider(identityProvider);
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
