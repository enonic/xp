package com.enonic.wem.portal;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.resource.Finder;
import org.restlet.resource.ServerResource;
import org.restlet.routing.Router;
import org.restlet.routing.TemplateRoute;
import org.restlet.routing.Variable;

import com.enonic.wem.portal.restlet.FinderFactory;

@Singleton
public final class PortalApplication
    extends Application
{
    @Inject
    protected FinderFactory finderFactory;

    @Override
    public Restlet createInboundRoot()
    {
        final Router underscoreRouter = new Router( getContext() );
        underscoreRouter.setDefaultMatchingMode( Router.MODE_FIRST_MATCH );

        // attach( underscoreRouter, "/service/{module}/{service}", ServiceResource.class );
        // attach( underscoreRouter, "/public/{module}/{resource}", PublicResource.class, "resource" );
        // attach( underscoreRouter, "/image/id/{id}", ImageByIdResource.class, "id" );
        // attach( underscoreRouter, "/image/{fileName}", ImageByNameResource.class, "fileName" );
        // attach( underscoreRouter, "/component/{component}", ComponentResource.class, "component" );

        final Router contentRouter = new Router( getContext() );
        contentRouter.setDefaultMatchingMode( Router.MODE_FIRST_MATCH );
        attach( contentRouter, "/{path}/_", underscoreRouter, "path" );
        // attach( contentRouter, "/{path}", ContentResource.class, "path" );

        final Router router = new Router( getContext() );
        attach( router, "/{mode}", contentRouter );

        return router;
    }

    private void attach( final Router router, final String template, final Class<? extends ServerResource> type,
                         final String... greedyVars )
    {
        final Finder finder = this.finderFactory.finder( type );
        attach( router, template, finder, greedyVars );
    }

    private void attach( final Router router, final String template, final Restlet restlet, final String... greedyVars )
    {
        final TemplateRoute route = router.attach( template, restlet );
        greedyVariable( route, greedyVars );
    }

    private void greedyVariable( final TemplateRoute route, final String... names )
    {
        final Map<String, Variable> map = route.getTemplate().getVariables();
        for ( final String name : names )
        {
            map.put( name, new Variable( Variable.TYPE_URI_PATH ) );
        }
    }
}
