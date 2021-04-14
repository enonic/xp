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
import com.enonic.xp.project.ProjectConstants;

@Provider
public final class CmsResourceFilter
    implements ContainerRequestFilter
{
    private static final Pattern PATTERN = Pattern.compile( "^/" + ResourceConstants.REST_ROOT + ResourceConstants.CMS_PATH );

    @Override
    public void filter( final ContainerRequestContext requestContext )
        throws IOException
    {
        final Matcher matcher = PATTERN.matcher( requestContext.getUriInfo().getPath() );
        if ( matcher.find() )
        {
            final String project = matcher.group( 1 );
            final Context context = ContextBuilder.
                from( ContextAccessor.current() ).
                repositoryId( ProjectConstants.PROJECT_REPO_ID_PREFIX + project ).
                build();
            ContextAccessor.INSTANCE.set( context );
        }
    }
}

