package com.enonic.xp.portal.impl.filter;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.graalvm.polyglot.Context;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.impl.JSContextFactory;
import com.enonic.xp.script.impl.util.JavascriptHelperFactory;
import com.enonic.xp.script.impl.value.ScriptValueFactory;
import com.enonic.xp.script.impl.value.ScriptValueFactoryImpl;
import com.enonic.xp.web.HttpMethod;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PortalRequestSerializerTest
{
    private ScriptValueFactory factory;

    private Context context;

    @BeforeEach
    public void setup()
    {
        this.context = JSContextFactory.create( PortalRequestSerializerTest.class.getClassLoader() );

        final JavascriptHelperFactory factory = new JavascriptHelperFactory( context );
        this.factory = new ScriptValueFactoryImpl( factory.create() );
    }

    @AfterEach
    public void destroy()
    {
        context.close();
    }

    @Test
    public void serializeComplete()
        throws Exception
    {
        final PortalRequest sourceRequest = new PortalRequest();
        final String jsonRequest = readResource( "PortalRequestSerializer_request.json" );
        final Object obj = execute( "var result = " + jsonRequest + "; result;" );
        final ScriptValue value = this.factory.newValue( obj );

        PortalRequestSerializer reqSerializer = new PortalRequestSerializer( sourceRequest, value );

        final PortalRequest portalRequest = reqSerializer.serialize();

        assertNotNull( portalRequest );
        assertEquals( HttpMethod.POST, portalRequest.getMethod() );
        assertEquals( "https", portalRequest.getScheme() );
        assertEquals( "myhost", portalRequest.getHost() );
        assertEquals( 1234, portalRequest.getPort() );
        assertEquals( "/some/path", portalRequest.getPath() );
        assertEquals( "https://myhost:1234/some/path/url", portalRequest.getUrl() );
        assertEquals( "127.0.0.42", portalRequest.getRemoteAddress() );
        assertEquals( RenderMode.EDIT, portalRequest.getMode() );
        assertEquals( true, portalRequest.isValidTicket() );
        assertEquals( Branch.from( "draft" ), portalRequest.getBranch() );
        assertEquals( "application/json", portalRequest.getContentType() );
        assertEquals( "POST BODY", portalRequest.getBody() );

        assertEquals( 2, portalRequest.getHeaders().size() );
        assertEquals( "header-value", portalRequest.getHeaders().get( "header" ) );
        assertEquals( "header-value2", portalRequest.getHeaders().get( "header2" ) );

        assertEquals( 2, portalRequest.getCookies().size() );
        assertEquals( "cookie-value", portalRequest.getCookies().get( "cookie" ) );
        assertEquals( "cookie-value2", portalRequest.getCookies().get( "cookie2" ) );

        assertEquals( 2, portalRequest.getParams().size() );
        assertEquals( "param-value", portalRequest.getParams().get( "param" ).iterator().next() );
        assertEquals( "param-value2", portalRequest.getParams().get( "param2" ).iterator().next() );
    }

    @Test
    public void serializeNoHeadersNoParams()
        throws Exception
    {
        final PortalRequest sourceRequest = new PortalRequest();
        final String jsonRequest = readResource( "PortalRequestSerializer_request2.json" );
        final Object obj = execute( "var result = " + jsonRequest + "; result;" );
        final ScriptValue value = this.factory.newValue( obj );

        PortalRequestSerializer reqSerializer = new PortalRequestSerializer( sourceRequest, value );

        final PortalRequest portalRequest = reqSerializer.serialize();

        assertNotNull( portalRequest );
        assertEquals( HttpMethod.POST, portalRequest.getMethod() );
        assertEquals( "https", portalRequest.getScheme() );
        assertEquals( "myhost", portalRequest.getHost() );
        assertEquals( 1234, portalRequest.getPort() );
        assertEquals( "/some/path", portalRequest.getPath() );
        assertEquals( "https://myhost:1234/some/path/url", portalRequest.getUrl() );
        assertEquals( "127.0.0.42", portalRequest.getRemoteAddress() );
        assertEquals( RenderMode.EDIT, portalRequest.getMode() );
        assertEquals( true, portalRequest.isValidTicket() );
        assertEquals( Branch.from( "draft" ), portalRequest.getBranch() );
        assertEquals( "application/json", portalRequest.getContentType() );

        assertEquals( 0, portalRequest.getHeaders().size() );
        assertEquals( 0, portalRequest.getCookies().size() );
        assertEquals( 0, portalRequest.getParams().size() );
    }

    @Test
    public void serializeNonObject()
        throws Exception
    {
        final PortalRequest sourceRequest = new PortalRequest();
        final Object obj = execute( "var result = 'response'; result;" );
        final ScriptValue value = this.factory.newValue( obj );

        PortalRequestSerializer reqSerializer = new PortalRequestSerializer( sourceRequest, value );

        final PortalRequest portalRequest = reqSerializer.serialize();

        assertNotNull( portalRequest );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void serializeMultiParams()
        throws Exception
    {
        final PortalRequest sourceRequest = new PortalRequest();
        final String jsonRequest = readResource( "PortalRequestSerializer_request3.json" );
        final Object obj = execute( "var result = " + jsonRequest + "; result;" );
        final ScriptValue value = this.factory.newValue( obj );

        PortalRequestSerializer reqSerializer = new PortalRequestSerializer( sourceRequest, value );

        final PortalRequest portalRequest = reqSerializer.serialize();

        assertNotNull( portalRequest );
        final Map<String, Object> bodyAsMap = (Map<String, Object>) portalRequest.getBody();
        assertEquals( "value1", bodyAsMap.get( "key1" ) );
        assertEquals( 42L, bodyAsMap.get( "key2" ) );

        assertEquals( 0, portalRequest.getHeaders().size() );
        assertEquals( 0, portalRequest.getCookies().size() );
        assertEquals( 4, portalRequest.getParams().size() );
        assertEquals( "param-value", portalRequest.getParams().get( "param" ).iterator().next() );
        final List<String> param2Values = new ArrayList<>( portalRequest.getParams().get( "param2" ) );
        assertEquals( "param-value2-a", param2Values.get( 0 ) );
        assertEquals( "param-value2-b", param2Values.get( 1 ) );
        assertEquals( "param-value2-c", param2Values.get( 2 ) );
    }

    private Object execute( final String script )
        throws Exception
    {
        return this.context.eval( "js", script );
    }

    private String readResource( final String resourceName )
        throws Exception
    {
        try (InputStream stream = getClass().getResourceAsStream( resourceName ))
        {
            return new String( stream.readAllBytes(), StandardCharsets.UTF_8 );
        }
    }
}
