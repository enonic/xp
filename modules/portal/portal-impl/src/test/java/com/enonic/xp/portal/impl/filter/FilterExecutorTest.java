package com.enonic.xp.portal.impl.filter;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.rendering.RenderException;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.site.filter.FilterDescriptor;
import com.enonic.xp.site.filter.FilterType;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class FilterExecutorTest
{

    @Test
    public void testExecuteResponseFilter()
        throws Exception
    {
        final PortalScriptService scriptService = Mockito.mock( PortalScriptService.class );
        final ScriptExports scriptExports = Mockito.mock( ScriptExports.class );
        when( scriptExports.hasMethod( "responseFilter" ) ).thenReturn( true );
        when( scriptService.execute( any( ResourceKey.class ) ) ).thenReturn( scriptExports );

        final FilterExecutor filterExecutor = new FilterExecutor( scriptService );

        final FilterDescriptor filter = FilterDescriptor.create().
            application( ApplicationKey.from( "myApp" ) ).
            name( "filter1" ).
            type( FilterType.RESPONSE ).
            build();
        final PortalRequest request = new PortalRequest();
        final PortalResponse response = PortalResponse.create().build();
        final PortalResponse filteredResponse = filterExecutor.executeResponseFilter( filter, request, response );

        assertNotNull( filteredResponse );
    }

    @Test
    public void testExecuteResponseFilterNotImplementingMethod()
        throws Exception
    {
        final PortalScriptService scriptService = Mockito.mock( PortalScriptService.class );
        final ScriptExports scriptExports = Mockito.mock( ScriptExports.class );
        when( scriptService.execute( any( ResourceKey.class ) ) ).thenReturn( scriptExports );

        final FilterExecutor filterExecutor = new FilterExecutor( scriptService );

        final FilterDescriptor filter = FilterDescriptor.create().
            application( ApplicationKey.from( "myApp" ) ).
            name( "filter1" ).
            type( FilterType.RESPONSE ).
            build();
        final PortalRequest request = new PortalRequest();
        final PortalResponse response = PortalResponse.create().build();

        try
        {
            filterExecutor.executeResponseFilter( filter, request, response );
            fail( "Expected exception" );
        }
        catch ( RenderException e )
        {
            assertEquals( "Missing exported function [responseFilter] in response filter [/site/filters/filter1.js]", e.getMessage() );
        }
    }
}