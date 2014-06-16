package com.enonic.wem.portal.base;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;

import org.mockito.Mockito;

import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.LowLevelAppDescriptor;
import com.sun.jersey.test.framework.spi.container.inmemory.InMemoryTestContainerFactory;

import com.enonic.wem.core.web.servlet.ServletRequestHolder;

public abstract class BaseHandlerTest<T extends BaseHandler>
    extends JerseyTest
{
    protected T resource;

    public BaseHandlerTest()
    {
        super( new InMemoryTestContainerFactory() );
    }

    @Override
    protected AppDescriptor configure()
    {
        this.resource = createResource();
        final DefaultResourceConfig config = new DefaultResourceConfig();
        config.getSingletons().add( this.resource );
        return new LowLevelAppDescriptor.Builder( config ).build();
    }

    protected final void mockCurrentContextHttpRequest()
    {
        final HttpServletRequest req = Mockito.mock( HttpServletRequest.class );
        Mockito.when( req.getScheme() ).thenReturn( "http" );
        Mockito.when( req.getServerName() ).thenReturn( "localhost" );
        Mockito.when( req.getLocalPort() ).thenReturn( 80 );
        ServletRequestHolder.setRequest( req );
    }

    protected abstract T createResource();

    protected final ClientResponse executeGet( final String path )
        throws Exception
    {
        final URI uri = new URI( path );
        final ClientRequest request = ClientRequest.create().build( uri, "GET" );
        return client().handle( request );
    }

    protected final ClientResponse executePost( final String path )
        throws Exception
    {
        final URI uri = new URI( path );
        final ClientRequest request = ClientRequest.create().build( uri, "POST" );
        return client().handle( request );
    }
}
