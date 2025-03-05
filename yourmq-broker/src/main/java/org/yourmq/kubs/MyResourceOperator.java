//package org.yourmq.kubs;
//
//import io.fabric8.kubernetes.api.model.Config;
//import io.fabric8.kubernetes.api.model.ObjectMeta;
//import io.fabric8.kubernetes.api.model.apiextensions.v1.CustomResourceDefinition;
//import io.fabric8.kubernetes.client.DefaultKubernetesClient;
//import io.fabric8.kubernetes.client.KubernetesClient;
//import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
//import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
//import io.fabric8.kubernetes.client.informers.cache.Lister;
//import io.fabric8.kubernetes.client.utils.Serialization;
//import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//
//// 定义自定义资源类
//class MyResource {
//    private ObjectMeta metadata;
//    private Map<String, Object> spec;
//    private Map<String, Object> status;
//
//    // Getters and Setters
//    public ObjectMeta getMetadata() {
//        return metadata;
//    }
//
//    public void setMetadata(ObjectMeta metadata) {
//        this.metadata = metadata;
//    }
//
//    public Map<String, Object> getSpec() {
//        return spec;
//    }
//
//    public void setSpec(Map<String, Object> spec) {
//        this.spec = spec;
//    }
//
//    public Map<String, Object> getStatus() {
//        return status;
//    }
//
//    public void setStatus(Map<String, Object> status) {
//        this.status = status;
//    }
//}
//
//public class MyResourceOperator {
//    private static final String GROUP = "example.com";
//    private static final String VERSION = "v1alpha1";
//    private static final String PLURAL = "myresources";
//    private static final String KIND = "MyResource";
//
//    public static void main(String[] args) {
//        Config config = Config.autoConfigure(null);
//        try (KubernetesClient client = new DefaultKubernetesClient(config)) {
//            // 创建自定义资源定义上下文
//            CustomResourceDefinitionContext crdContext = new CustomResourceDefinitionContext.Builder()
//                   .withGroup(GROUP)
//                   .withVersion(VERSION)
//                   .withPlural(PLURAL)
//                   .withScope("Namespaced")
//                   .build();
//
//            // 获取自定义资源定义
//            CustomResourceDefinition crd = client.apiextensions().v1().customResourceDefinitions()
//                   .load(MyResourceOperator.class.getResourceAsStream("/mycrd.yaml")).get();
//
//            // 创建共享信息工厂
//            SharedInformerFactory informerFactory = client.informers();
//            SharedIndexInformer<MyResource> informer = informerFactory.sharedIndexInformerForCustomResource(crdContext, MyResource.class, 30 * 60 * 1000L);
//            Lister<MyResource> lister = new Lister<>(informer.getIndexer());
//
//            // 启动信息工厂
//            informerFactory.startAllRegisteredInformers();
//
//            // 创建调度器，定期处理自定义资源
//            ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
//            executorService.scheduleAtFixedRate(() -> {
//                lister.list().forEach(myResource -> {
//                    try {
//                        handleMyResource(client, crdContext, myResource);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                });
//            }, 0, 10, TimeUnit.SECONDS);
//        }
//    }
//
//    private static void handleMyResource(KubernetesClient client, CustomResourceDefinitionContext crdContext, MyResource myResource) {
//        String namespace = myResource.getMetadata().getNamespace();
//        String name = myResource.getMetadata().getName();
//        System.out.println("Handling MyResource: " + namespace + "/" + name);
//
//        // 获取 spec 中的 message
//        String message = (String) myResource.getSpec().get("message");
//        System.out.println("Message: " + message);
//
//        // 更新状态
//        Map<String, Object> status = new HashMap<>();
//        status.put("phase", "Processed");
//        myResource.setStatus(status);
//
//        // 更新自定义资源
//        client.customResources(crdContext, MyResource.class).inNamespace(namespace).withName(name).replace(myResource);
//    }
//}