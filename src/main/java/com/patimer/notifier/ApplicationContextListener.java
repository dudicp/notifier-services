package com.patimer.notifier;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ApplicationContextListener implements ServletContextListener
{
    private final static Logger log = LogManager.getLogger(ApplicationContextListener.class);

    public void contextInitialized(ServletContextEvent servletContextEvent)
    {
        log.info("Notifier-Services application context started successfully.");
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent)
    {
        log.info("Notifier-Services application context destroyed.");
    }
}
