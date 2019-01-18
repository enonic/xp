package com.enonic.xp.impl.server.rest;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPaths;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.FindContentByParentParams;
import com.enonic.xp.content.FindContentByParentResult;
import com.enonic.xp.impl.server.rest.model.ReprocessContentRequestJson;
import com.enonic.xp.impl.server.rest.model.ReprocessContentResultJson;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.security.RoleKeys;

@Path("/content")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_ID)
@Component(immediate = true, property = "group=api")
public final class ContentResource
    implements JaxRsComponent
{
    private ContentService contentService;

    private final static Logger LOG = LoggerFactory.getLogger( ContentResource.class );

    @POST
    @Path("reprocess")
    public ReprocessContentResultJson reprocess( final ReprocessContentRequestJson request )
    {
        final List<ContentPath> updated = new ArrayList<>();
        final List<String> errors = new ArrayList<>();

        final Content content = this.contentService.getByPath( request.getSourceBranchPath().getContentPath() );
        try
        {
            reprocessContent( content, request.isSkipChildren(), updated, errors );
        }
        catch ( Exception e )
        {
            errors.add(
                String.format( "Content '%s' - %s: %s", content.getPath().toString(), e.getClass().getCanonicalName(), e.getMessage() ) );
            LOG.warn( "Error reprocessing content [" + content.getPath() + "]", e );
        }

        return new ReprocessContentResultJson( ContentPaths.from( updated ), errors );
    }

    private void reprocessContent( final Content content, final boolean skipChildren, final List<ContentPath> updated,
                                   final List<String> errors )
    {
        final Content reprocessedContent = this.contentService.reprocess( content.getId() );
        if ( !reprocessedContent.equals( content ) )
        {
            updated.add( content.getPath() );
        }
        if ( skipChildren )
        {
            return;
        }

        int from = 0;
        int resultCount;
        do
        {
            final FindContentByParentParams findParams = FindContentByParentParams.create().parentId( content.getId() ).
                from( from ).size( 5 ).build();
            final FindContentByParentResult results = this.contentService.findByParent( findParams );

            for ( Content child : results.getContents() )
            {
                try
                {
                    reprocessContent( child, false, updated, errors );
                }
                catch ( Exception e )
                {
                    errors.add( String.format( "Content '%s' - %s: %s", child.getPath().toString(), e.getClass().getCanonicalName(),
                                               e.getMessage() ) );
                    LOG.warn( "Error reprocessing content [" + child.getPath() + "]", e );
                }
            }
            resultCount = Math.toIntExact( results.getHits() );
            from = from + resultCount;
        }
        while ( resultCount > 0 );
    }

    @SuppressWarnings("UnusedDeclaration")
    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }
}
