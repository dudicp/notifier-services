package com.patimer.notifier.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:unit-test-context.xml", "classpath:application-core-context.xml"})
public abstract class AbstractDaoTest
{
    private final static Logger log = LogManager.getLogger(AbstractDaoTest.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Before
    public void setUp()
    {
        String databaseName = mongoTemplate.getDb().getName();
        log.info("SetUp: dropping entire database: '" + databaseName +"'.");

        mongoTemplate.getDb().dropDatabase();
    }

    @After
    public void tearDown()
    {
        String databaseName = mongoTemplate.getDb().getName();
        log.info("TearDown: dropping entire database: '" + databaseName +"'.");

        mongoTemplate.getDb().dropDatabase();
    }
}
