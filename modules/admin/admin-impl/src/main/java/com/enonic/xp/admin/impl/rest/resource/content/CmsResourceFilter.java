package com.enonic.xp.admin.impl.rest.resource.content;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.layer.ContentLayerName;


@Provider
public class CmsResourceFilter
    implements ContainerRequestFilter
{
    private static final Pattern PATTERN = Pattern.compile( "^/" + ResourceConstants.REST_ROOT + "cms/([^/]+)/([^/]+)" );

    @Override
    public void filter( final ContainerRequestContext requestContext )
        throws IOException
    {
        final Matcher matcher = PATTERN.matcher( requestContext.getUriInfo().getPath() );
        if ( matcher.find() )
        {
            final String project = matcher.group( 1 );
            final String layer = matcher.group( 2 );
            final Context context = ContextBuilder.
                from( ContextAccessor.current() ).
                repositoryId( "com.enonic.cms." + project ).
                branch( ContentLayerName.from( layer ).getDraftBranch() ).
                build();
            ContextAccessor.INSTANCE.set( context );
            System.out.println( "project" + matcher.group( 1 ) );
        }
    }
}
