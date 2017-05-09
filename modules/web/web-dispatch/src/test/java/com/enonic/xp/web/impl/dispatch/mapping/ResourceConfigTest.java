package com.enonic.xp.web.impl.dispatch.mapping;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.collections.Iterables;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import static org.junit.Assert.*;

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

        final List<String> paramNames = Lists.newArrayList( Iterables.toIterable( config.getInitParameterNames() ) );
        assertEquals( "[a, b]", paramNames.toString() );
    }
}
