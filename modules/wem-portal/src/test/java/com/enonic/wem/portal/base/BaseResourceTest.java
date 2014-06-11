package com.enonic.wem.portal.base;

import org.junit.Before;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.resource.Finder;

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
}
