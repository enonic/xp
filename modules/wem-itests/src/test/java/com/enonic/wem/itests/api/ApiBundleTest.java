package com.enonic.wem.itests.api;

import java.util.List;

import org.junit.Test;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;

import junit.framework.Assert;

import com.enonic.wem.itests.AbstractOsgiTest;

public class ApiBundleTest
    extends AbstractOsgiTest
{
    @Override
    protected void options( final List<Option> options )
    {
        super.options( options );
        options.add( CoreOptions.mavenBundle( "com.enonic.wem", "wem-api" ).versionAsInProject() );
    }

    @Test
    public void testBundle()
    {
        Assert.assertTrue( true );
    }
}
