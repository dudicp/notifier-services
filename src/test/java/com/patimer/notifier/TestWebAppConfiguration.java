package com.patimer.notifier;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

/**
 * This test tries to fully initialized Spring's Web-Application Context in order to make sure the configurations are
 * valid (the configurations files should be provided in the @ContextConfiguration annotation).
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:application-core-context.xml", "classpath:application-db-context.xml"})
@WebAppConfiguration
public class TestWebAppConfiguration
{
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    public void testWebAppConfiguration()
    {
        // given - nothing

        // when - Spring's Web-Application Context is loaded automatically as part of the test (SpringJUnit4ClassRunner).

        // then - exception will be thrown when the context initializtion failed.
    }

}
