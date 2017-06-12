package com.enonic.xp.launcher.impl.util;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;

import static org.junit.Assert.*;

public class OsgiExportsBuilderTest
{
    private OsgiExportsBuilder builder;


    @Before
    public void setup()
    {
        this.builder = new OsgiExportsBuilder( getClass().getClassLoader() );
    }

    @Test
    public void expandExports()
    {
        final String exports = this.builder.expandExports( "org.slf4j" );
        assertEquals( "org.slf4j", exports );
    }

    @Test
    public void expandExports_options()
    {
        final String exports = this.builder.expandExports( "org.slf4j;version=1.0" );
        assertEquals( "org.slf4j;version=1.0", exports );
    }

    @Test
    public void expandExports_wildcard()
    {
        final String exports = this.builder.expandExports( "org.slf4j.*" );
        assertEquals( "org.slf4j,org.slf4j.bridge,org.slf4j.event,org.slf4j.helpers,org.slf4j.impl,org.slf4j.spi", sort( exports ) );
    }

    private String sort( final String str )
    {
        return Joiner.on( ',' ).join( Sets.newTreeSet( Splitter.on( ',' ).split( str ) ) );
    }
}
