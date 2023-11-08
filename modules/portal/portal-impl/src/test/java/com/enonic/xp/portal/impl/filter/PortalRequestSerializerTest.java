package com.enonic.xp.portal.impl.filter;

import java.io.Closeable;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.script.ScriptFixturesFacade;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.impl.value.ScriptValueFactory;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.IdProviderKeys;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PortalRequestSerializerTest
{
    private ScriptValueFactory<?> factory;

    private HttpServletRequest rawRequest;

    @BeforeEach
    public void setup()
    {
        this.factory = ScriptFixturesFacade.getInstance().scriptValueFactory();
        this.rawRequest = mock( HttpServletRequest.class );

        final VirtualHost virtualHost = mock( VirtualHost.class );
        when( virtualHost.getIdProviderKeys() ).thenReturn( IdProviderKeys.from( "otherEnabledIdProvider" ) );

        VirtualHostHelper.setVirtualHost( rawRequest, initVirtualHost( rawRequest, virtualHost ) );
    }

    @AfterEach
    public void destroy()
        throws Exception
    {
        if ( factory instanceof Closeable )
        {
            ( (Closeable) factory ).close();
        }
    }

    @Test
    public void serializeComplete()
        throws Exception
    {
        final PortalRequest sourceRequest = new PortalRequest();
        sourceRequest.setRawRequest( this.rawRequest );
        final String jsonRequest = readResource( "PortalRequestSerializer_request.json" );
        final ScriptValue value = factory.evalValue( "var result = " + jsonRequest + "; result;" );

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
        sourceRequest.setRawRequest( this.rawRequest );
        final String jsonRequest = readResource( "PortalRequestSerializer_request2.json" );
        final ScriptValue value = factory.evalValue( "var result = " + jsonRequest + "; result;" );

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
        sourceRequest.setRawRequest( this.rawRequest );
        final ScriptValue value = factory.evalValue( "var result = 'response'; result;" );

        PortalRequestSerializer reqSerializer = new PortalRequestSerializer( sourceRequest, value );

        final PortalRequest portalRequest = reqSerializer.serialize();

        assertNotNull( portalRequest );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void serializeMultiParams()
        throws Exception
    {
        final PortalRequest sourceRequest = new PortalRequest();
        sourceRequest.setRawRequest( this.rawRequest );
        final String jsonRequest = readResource( "PortalRequestSerializer_request3.json" );
        final ScriptValue value = factory.evalValue( "var result = " + jsonRequest + "; result;" );

        PortalRequestSerializer reqSerializer = new PortalRequestSerializer( sourceRequest, value );

        final PortalRequest portalRequest = reqSerializer.serialize();

        assertNotNull( portalRequest );
        final Map<String, Object> bodyAsMap = (Map<String, Object>) portalRequest.getBody();
        assertEquals( "value1", bodyAsMap.get( "key1" ) );
        assertEquals( 42, bodyAsMap.get( "key2" ) );

        assertEquals( 0, portalRequest.getHeaders().size() );
        assertEquals( 0, portalRequest.getCookies().size() );
        assertEquals( 4, portalRequest.getParams().size() );
        assertEquals( "param-value", portalRequest.getParams().get( "param" ).iterator().next() );
        final List<String> param2Values = new ArrayList<>( portalRequest.getParams().get( "param2" ) );
        assertEquals( "param-value2-a", param2Values.get( 0 ) );
        assertEquals( "param-value2-b", param2Values.get( 1 ) );
        assertEquals( "param-value2-c", param2Values.get( 2 ) );
    }

    @Test
    public void serialize_no_params_duplication()
        throws Exception
    {
        final PortalRequest sourceRequest = new PortalRequest();
        sourceRequest.setRawRequest( this.rawRequest );
        sourceRequest.getParams().put( "a", "oldA" );

        sourceRequest.getParams().put( "b", "oldB" );
        sourceRequest.getParams().put( "b", "oldB" );

        sourceRequest.getParams().put( "d", "oldD" );
        sourceRequest.getParams().put( "e", "oldE" );
        sourceRequest.getParams().put( "f", "oldF" );

        final String jsonRequest = readResource( "PortalRequestSerializer_request4.json" );
        final ScriptValue value = factory.evalValue( "var result = " + jsonRequest + "; result;" );

        PortalRequestSerializer reqSerializer = new PortalRequestSerializer( sourceRequest, value );

        final PortalRequest portalRequest = reqSerializer.serialize();

        assertThat( portalRequest.getParams().get( "a" ) ).containsExactly( "newA" );
        assertThat( portalRequest.getParams().get( "b" ) ).containsExactly( "newB", "1" );
        assertThat( portalRequest.getParams().get( "c" ) ).containsExactly( "newC" );
        assertThat( portalRequest.getParams().get( "d" ) ).containsExactly( "oldD" );
        assertFalse( portalRequest.getParams().containsKey( "e" ) );
        assertFalse( portalRequest.getParams().containsKey( "f" ) );
    }

    private String readResource( final String resourceName )
        throws Exception
    {
        try (InputStream stream = getClass().getResourceAsStream( resourceName ))
        {
            return new String( Objects.requireNonNull( stream ).readAllBytes(), StandardCharsets.UTF_8 );
        }
    }

    private VirtualHost initVirtualHost( final HttpServletRequest rawRequest, final VirtualHost virtualHost )
    {
        when( rawRequest.getAttribute( isA( String.class ) ) ).thenAnswer(
            ( InvocationOnMock invocation ) -> VirtualHost.class.getName().equals( invocation.getArguments()[0] )
                ? virtualHost
                : generateDefaultVirtualHost() );

        return virtualHost;
    }

    private VirtualHost generateDefaultVirtualHost()
    {
        VirtualHost result = mock( VirtualHost.class );

        when( result.getHost() ).thenReturn( "host" );
        when( result.getSource() ).thenReturn( "/" );
        when( result.getTarget() ).thenReturn( "/" );
        when( result.getIdProviderKeys() ).thenReturn( IdProviderKeys.from( IdProviderKey.system() ) );

        return result;
    }
}
