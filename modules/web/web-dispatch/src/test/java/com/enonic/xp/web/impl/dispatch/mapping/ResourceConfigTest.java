package com.enonic.xp.web.impl.dispatch.mapping;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletContext;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.common.collect.ImmutableMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

public class ResourceConfigTest
{
    @Test
    public void testGetters()
    {
        final ServletContext context = Mockito.mock( ServletContext.class );
        final Map<String, String> initParams = new ImmutableMap.Builder<String, String>().
            put( "a", "1" ).
            put( "b", "2" ).
            build();

        final ResourceConfig config = new ResourceConfig( "test", context, initParams );
        assertEquals( "test", config.getFilterName() );
        assertEquals( "test", config.getServletName() );
        assertSame( context, config.getServletContext() );

        assertEquals( "1", config.getInitParameter( "a" ) );
        assertEquals( "2", config.getInitParameter( "b" ) );
        assertNull( config.getInitParameter( "c" ) );

        final List<String> paramNames = Collections.list( config.getInitParameterNames() );
        assertEquals( "[a, b]", paramNames.toString() );
    }
}
