package com.example;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.view.DesignDocument;
import org.junit.Assert;;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class TestcontainerSpringBootCouchbaseApplicationTests extends AbstractSpringBootTestConfig {

	@Autowired
	private Bucket bucket;

	@Test
	public void testRepository() {
		List<DesignDocument> designDocuments = bucket.bucketManager().getDesignDocuments();
		Assert.assertNotNull(designDocuments);
	}

}
