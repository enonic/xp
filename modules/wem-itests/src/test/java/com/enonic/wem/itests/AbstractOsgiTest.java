package com.enonic.wem.itests;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.AllConfinedStagedReactorFactory;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public abstract class AbstractOsgiTest
{
    @Configuration
    public final Option[] config()
    {
        final List<Option> options = new ArrayList<>();
        options( options );
        return options.toArray( new Option[options.size()] );
    }

    protected void options( final List<Option> options )
    {
        options.add( systemProperty( "org.ops4j.pax.logging.DefaultServiceLog.level" ).value( "WARN" ) );
        options.add( junitBundles() );
    }
}
