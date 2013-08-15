package com.enonic.wem.admin.rest.resource.content;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Joiner;
import com.sun.jersey.api.NotFoundException;

import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.admin.rest.resource.content.model.ContentListJson;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.GetContentVersion;
import com.enonic.wem.api.command.content.GetContents;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentIds;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.versioning.ContentVersionId;

@Path("content")
public class ContentResource
    extends AbstractResource
{
    @GET
    public ContentListJson get( @QueryParam("path") final String path,
                                @QueryParam("version") final Integer version,
                                @QueryParam("contentIds") final List<String> contentIds )
    {
        if ( StringUtils.isNotEmpty( path ) )
        {
            final ContentPath contentPath = ContentPath.from( path );
            return handleGetContentByPath( contentPath, version );
        }
        else
        {
            final String[] stringOfPathIds = contentIds.toArray( new String[contentIds.size()] );
            return handleGetContentByIds( ContentIds.from( stringOfPathIds ) );
        }
    }

    private ContentListJson handleGetContentByPath( final ContentPath contentPath, final Integer version )
    {
        final Content content;

        if ( version == null )
        {
            content = findContent( contentPath );
        }
        else
        {
            content = findContentVersion( contentPath, ContentVersionId.of( version ) );
        }

        if ( content != null )
        {
            return new ContentListJson( content );
        }
        throw new NotFoundException( String.format( "Content [%s] was not found", contentPath ) );
    }

    private Content findContent( final ContentPath contentPath )
    {
        final GetContents getContents = Commands.content().get();
        getContents.selectors( ContentPaths.from( contentPath ) );

        final Contents contents = client.execute( getContents );
        return contents.isNotEmpty() ? contents.first() : null;
    }

    private Content findContentVersion( final ContentPath contentPath, final ContentVersionId versionId )
    {
        final GetContentVersion getContentVersion = Commands.content().getVersion();
        getContentVersion.selector( contentPath ).version( versionId );
        return client.execute( getContentVersion );
    }

    private ContentListJson handleGetContentByIds( final ContentIds ids )
    {
        final GetContents getContents = Commands.content().get();
        getContents.selectors( ids );

        final Contents contents = client.execute( getContents );

        if ( contents.isNotEmpty() )
        {
            return new ContentListJson( contents );
        }

        final String missing = Joiner.on( "," ).join( ids );
        throw new NotFoundException( String.format( "Contents [%s] not found", missing ) );
    }

    // list() @GET
    // delete() @POST
    // create() @POST
    // update() @POST
}
