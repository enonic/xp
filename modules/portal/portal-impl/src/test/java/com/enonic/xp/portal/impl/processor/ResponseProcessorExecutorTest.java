package com.enonic.xp.portal.impl.processor;

import java.nio.charset.StandardCharsets;

import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.mapper.PortalRequestMapper;
import com.enonic.xp.portal.impl.rendering.RenderException;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.site.processor.ResponseProcessorDescriptor;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class ResponseProcessorExecutorTest
{

    @Test
    public void testExecuteResponseFilter()
        throws Exception
    {
        final PortalScriptService scriptService = Mockito.mock( PortalScriptService.class );
        final ScriptExports scriptExports = Mockito.mock( ScriptExports.class );
        when( scriptExports.hasMethod( "responseFilter" ) ).thenReturn( true );
        when( scriptService.execute( any( ResourceKey.class ) ) ).thenReturn( scriptExports );

        final ResponseProcessorExecutor filterExecutor = new ResponseProcessorExecutor( scriptService );

        final ResponseProcessorDescriptor filter = ResponseProcessorDescriptor.create().
            application( ApplicationKey.from( "myApp" ) ).
            name( "filter1" ).
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

        final ResponseProcessorExecutor filterExecutor = new ResponseProcessorExecutor( scriptService );

        final ResponseProcessorDescriptor filter = ResponseProcessorDescriptor.create().
            application( ApplicationKey.from( "myApp" ) ).
            name( "filter1" ).
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
            assertEquals( "Missing exported function [responseFilter] in response filter [/site/processors/filter1.js]", e.getMessage() );
        }
    }

    @Test
    public void testExecuteResponseFilterWithByteSourceBody()
        throws Exception
    {
        final ByteSource data = ByteSource.wrap( "DATA".getBytes( StandardCharsets.UTF_8 ) );

        final PortalScriptService scriptService = Mockito.mock( PortalScriptService.class );
        final ScriptExports scriptExports = Mockito.mock( ScriptExports.class );
        when( scriptExports.hasMethod( "responseFilter" ) ).thenReturn( true );
        when( scriptService.execute( any( ResourceKey.class ) ) ).thenReturn( scriptExports );

        final ScriptValue result = Mockito.mock( ScriptValue.class );
        final ScriptValue body = Mockito.mock( ScriptValue.class );
        when( body.getValue() ).thenReturn( data.toString() );
        when( result.isObject() ).thenReturn( true );
        when( result.getMember( "body" ) ).thenReturn( body );
        when( scriptExports.executeMethod( anyString(), any( PortalRequestMapper.class ), any( PortalRequestMapper.class ) ) ).thenReturn(
            result );

        final ResponseProcessorExecutor filterExecutor = new ResponseProcessorExecutor( scriptService );

        final ResponseProcessorDescriptor filter = ResponseProcessorDescriptor.create().
            application( ApplicationKey.from( "myApp" ) ).
            name( "filter1" ).
            build();
        final PortalRequest request = new PortalRequest();

        final PortalResponse response = PortalResponse.create().body( data ).build();
        final PortalResponse filteredResponse = filterExecutor.executeResponseFilter( filter, request, response );

        assertNotNull( filteredResponse );
        assertTrue( filteredResponse.getBody() instanceof ByteSource );
        assertArrayEquals( data.read(), ( (ByteSource) filteredResponse.getBody() ).read() );
    }
}