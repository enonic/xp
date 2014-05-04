package com.enonic.wem.itests;

import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;

import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;

public abstract class AbstractTest
{
    @Configuration
    public Option[] config()
    {
        return options( systemProperty( "org.ops4j.pax.logging.DefaultServiceLog.level" ).value( "WARN" ), junitBundles() );
    }
}
