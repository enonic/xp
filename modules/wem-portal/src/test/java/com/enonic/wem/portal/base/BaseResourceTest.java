package com.enonic.wem.portal.base;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.mockito.Mockito;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.resource.Finder;

import com.enonic.wem.core.web.servlet.ServletRequestHolder;
import com.enonic.wem.portal.PortalApplication;

public abstract class BaseResourceTest<T extends BaseResource>
{
    protected T resource;

    private PortalApplication app;

    @Before
    public final void setup()
        throws Exception
    {
        configure();
        final Finder finder = new SingletonFinder( this.resource );
        final Finder notFoundFinder = new SingletonFinder( new NotFoundResource() );

        this.app = new PortalApplication();
        this.app.setFinderFactory( type -> {
            if ( resource.getClass() == type )
            {
                return finder;
            }
            else
            {
                return notFoundFinder;
            }
        } );
    }

    protected abstract void configure()
        throws Exception;

    protected final Response executeRequest( final Request request )
    {
        return this.app.handle( request );
    }

    protected void mockCurrentContextHttpRequest()
    {
        final HttpServletRequest req = Mockito.mock( HttpServletRequest.class );
        Mockito.when( req.getScheme() ).thenReturn( "http" );
        Mockito.when( req.getServerName() ).thenReturn( "localhost" );
        Mockito.when( req.getLocalPort() ).thenReturn( 80 );
        ServletRequestHolder.setRequest( req );
    }
}
