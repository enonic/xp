package com.enonic.xp.launcher.impl.util;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OsgiExportsBuilderTest
{
    private OsgiExportsBuilder builder;


    @BeforeEach
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
        return Stream.of( str.split( ",", -1 ) ).sorted().distinct().collect( Collectors.joining( "," ) );
    }
}
