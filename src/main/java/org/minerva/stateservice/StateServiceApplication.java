//package org.minerva.stateservice;
//
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//
//@SpringBootApplication
//public class StateServiceApplication {
//
//    public static void main(String[] args) {
//        KieBase kBase = new KieHelper().addResource(ResourceFactory.newClassPathResource("scriptTaskExample.bpmn")).build();
//        KieSession kiesession = kieBase.newKieSession();
//        ProcessInstance processInstance = kiesession.startProcess("LOS.scriptTaskExample");
//
//        EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
//
//        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
//                .newDefaultBuilder()
//                .addEnvironmentEntry(EnvironmentName.ENTITY_MANAGER_FACTORY, emf)
//                .addEnvironmentEntry(EnvironmentName.TRANSACTION_MANAGER, TransactionManagerServices.getTransactionManager())
//                .addAsset(ResourceFactory.newClassPathResource("scriptTaskExample.bpmn"), ResourceType.BPMN2)
//                .get();
//
//        RuntimeManager manager = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment);
//        RuntimeEngine runtimeEngine = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
//        KieSession kieSession = runtimeEngine.getKieSession();
//        try {
//            UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
//            ut.begin();
//            kieSession.insert(new Person("John Doe"));
//            kieSession.startProcess("LOS.scriptTaskExample");
//            ut.commit();
//        } catch (NamingException | SystemException | NotSupportedException | HeuristicRollbackException | HeuristicMixedException | RollbackException e) {
//            throw new RuntimeException(e);
//        }
//
//        SpringApplication.run(StateServiceApplication.class, args);
//    }
//
//}
