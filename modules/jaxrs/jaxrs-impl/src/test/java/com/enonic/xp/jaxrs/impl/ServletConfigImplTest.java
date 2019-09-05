package com.enonic.xp.jaxrs.impl;

import java.util.List;

import javax.servlet.ServletContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.collections.Iterables;

import com.google.common.collect.Lists;

import static org.junit.jupiter.api.Assertions.*;

public class ServletConfigImplTest
{
    private ServletConfigImpl context;

    @BeforeEach
    public void setup()
    {
        this.context = new ServletConfigImpl( "test", Mockito.mock( ServletContext.class ) );
    }

    @Test
    public void getServletName()
    {
        assertEquals( "test", this.context.getServletName() );
    }

    @Test
    public void getServletContext()
    {
        assertNotNull( this.context.getServletContext() );
    }

    @Test
    public void testInitParams()
    {
        final List<String> params1 = Lists.newArrayList( Iterables.toIterable( this.context.getInitParameterNames() ) );
        assertEquals( "[]", params1.toString() );

        this.context.setInitParameter( "a", "1" );

        final List<String> params2 = Lists.newArrayList( Iterables.toIterable( this.context.getInitParameterNames() ) );
        assertEquals( "[a]", params2.toString() );
        assertEquals( "1", this.context.getInitParameter( "a" ) );
    }
}
