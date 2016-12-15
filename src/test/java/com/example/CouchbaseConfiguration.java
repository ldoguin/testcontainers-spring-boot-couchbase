package com.example;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.bucket.BucketType;
import com.couchbase.client.java.cluster.BucketSettings;
import com.couchbase.client.java.cluster.ClusterInfo;
import com.couchbase.client.java.cluster.ClusterManager;
import com.couchbase.client.java.cluster.DefaultBucketSettings;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.PostConstruct;

/**
 * Created by ldoguin on 12/15/16.
 */
@Configuration
class CouchbaseConfiguration {

    private CouchbaseContainer couchbaseContainer;

    @PostConstruct
    public void init() throws Exception {
        couchbaseContainer = AbstractSpringBootTestConfig.couchbaseContainer;
        BucketSettings settings = DefaultBucketSettings.builder()
                .enableFlush(true).name("default").quota(100).replicas(0).type(BucketType.COUCHBASE).build();
        ClusterManager clusterManager = couchbaseContainer.geCouchbaseCluster().clusterManager(AbstractSpringBootTestConfig.clusterUser, AbstractSpringBootTestConfig.clusterPassword);
        if (!clusterManager.hasBucket("default")){
            settings =  clusterManager.insertBucket(settings);
            waitForContainer();
        }

    }

    public void waitForContainer(){
        CouchbaseWaitStrategy s = new CouchbaseWaitStrategy();
        s.withBasicCredentials(AbstractSpringBootTestConfig.clusterUser, AbstractSpringBootTestConfig.clusterPassword);
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
        ClusterManager manager = cc.clusterManager(AbstractSpringBootTestConfig.clusterUser, AbstractSpringBootTestConfig.clusterPassword);
        return manager.info();
    }

    @Bean
    @Primary
    public Bucket couchbaseClient() throws Exception {
        return couchbaseContainer.geCouchbaseCluster().openBucket("default");
    }

}
