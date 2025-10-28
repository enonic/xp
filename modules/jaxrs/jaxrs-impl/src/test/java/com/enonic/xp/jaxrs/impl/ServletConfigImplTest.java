package com.enonic.xp.jaxrs.impl;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.servlet.ServletContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ServletConfigImplTest
{
    private ServletConfigImpl context;

    @BeforeEach
    void setup()
    {
        this.context = new ServletConfigImpl( "test", Mockito.mock( ServletContext.class ) );
    }

    @Test
    void getServletName()
    {
        assertEquals( "test", this.context.getServletName() );
    }

    @Test
    void getServletContext()
    {
        assertNotNull( this.context.getServletContext() );
    }

    @Test
    void testInitParams()
    {
        final List<String> params1 = Collections.list( this.context.getInitParameterNames() );
        assertEquals( "[]", params1.toString() );

        this.context.setInitParameter( "a", "1" );

        final List<String> params2 = Collections.list( this.context.getInitParameterNames() );
        assertEquals( "[a]", params2.toString() );
        assertEquals( "1", this.context.getInitParameter( "a" ) );
    }
}
