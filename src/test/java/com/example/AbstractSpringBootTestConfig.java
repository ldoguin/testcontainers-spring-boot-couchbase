package com.example;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.bucket.BucketType;
import com.couchbase.client.java.cluster.BucketSettings;
import com.couchbase.client.java.cluster.ClusterInfo;
import com.couchbase.client.java.cluster.ClusterManager;
import com.couchbase.client.java.cluster.DefaultBucketSettings;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ldoguin on 12/13/16.
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestcontainerSpringBootCouchbaseApplication.class, AbstractSpringBootTestConfig.CouchbaseConfiguration.class})
@EnableAutoConfiguration(exclude = CouchbaseAutoConfiguration.class)
public abstract class AbstractSpringBootTestConfig {

    public static final String clusterUser = "Administrator";
    public static final String clusterPassword = "password";

    @ClassRule
    public static CouchbaseContainer couchbaseContainer = new CouchbaseContainer()
            .withFTS(true)
            .withIndex(true)
            .withQuery(true)
            .withBeerSample(true)
            .withClusterUsername(clusterUser)
            .withClusterPassword(clusterPassword);

    @TestConfiguration
    static class CouchbaseConfiguration {

        private CouchbaseContainer couchbaseContainer;

        @PostConstruct
        public void init() throws Exception {
            couchbaseContainer = AbstractSpringBootTestConfig.couchbaseContainer;
            BucketSettings settings = DefaultBucketSettings.builder()
                    .enableFlush(true).name("default").quota(100).replicas(0).type(BucketType.COUCHBASE).build();
            ClusterManager clusterManager = couchbaseContainer.geCouchbaseCluster().clusterManager(clusterUser, clusterPassword);
            if (!clusterManager.hasBucket("default")){
                settings =  clusterManager.insertBucket(settings);
                waitForContainer();
            }

        }

        public void waitForContainer(){
            CouchbaseWaitStrategy s = new CouchbaseWaitStrategy();
            s.withBasicCredentials(clusterUser, clusterPassword);
            s.waitUntilReady(couchbaseContainer);
        }


        @Bean
        @Primary
        public CouchbaseEnvironment couchbaseEnvironment() throws Exception {
            return couchbaseContainer.getCouchbaseEnvironnement();
        }

        @Bean
        @Primary
        public Cluster couchbaseCluster() throws Exception {
            return couchbaseContainer.geCouchbaseCluster();
        }

        @Bean
        @Primary
        public ClusterInfo couchbaseClusterInfo() throws Exception {
            Cluster cc = couchbaseCluster();
            ClusterManager manager = cc.clusterManager(clusterUser, clusterPassword);
            return manager.info();
        }

        @Bean
        @Primary
        public Bucket couchbaseClient() throws Exception {
            return couchbaseContainer.geCouchbaseCluster().openBucket("default");
        }

    }
}
