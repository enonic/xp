package com.enonic.xp.web.filter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FilterConfigInitParametersOverrideTest
{
    @Mock
    FilterConfig delegate;

    @Mock
    ServletContext servletContext;

    @Test
    void testDelegation()
    {
        when( delegate.getServletContext() ).thenReturn( servletContext );
        when( delegate.getFilterName() ).thenReturn( "testFilterName" );

        final FilterConfigInitParametersOverride instance = new FilterConfigInitParametersOverride( delegate, Map.of() );

        assertEquals( "testFilterName", instance.getFilterName() );
        assertSame( servletContext, instance.getServletContext() );

        verify( delegate, never() ).getInitParameterNames();
        verify( delegate, never() ).getInitParameter( any() );
    }

    @Test
    void testInitParameters()
    {
        final FilterConfigInitParametersOverride instance = new FilterConfigInitParametersOverride( delegate, Map.of( "a", "b" ) );

        assertEquals( "b", instance.getInitParameter( "a" ) );
        assertEquals( List.of( "a" ), Collections.list( instance.getInitParameterNames() ) );
    }
}
