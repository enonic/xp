package com.enonic.xp.ignite.impl.config;

import org.apache.ignite.IgniteLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class LoggerConfig
{
    static IgniteLogger create()
    {
        return new Log4JWrapper( LoggerFactory.getLogger( Logger.ROOT_LOGGER_NAME ) );
    }

}
