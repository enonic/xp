package com.enonic.wem.admin.rest.rpc.content;

import org.apache.commons.lang.StringUtils;

import com.enonic.wem.admin.json.JsonErrorResult;
import com.enonic.wem.admin.jsonrpc.JsonRpcContext;
import com.enonic.wem.admin.rest.rpc.AbstractDataRpcHandler;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.GetContentVersion;
import com.enonic.wem.api.command.content.GetContents;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentIds;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.versioning.ContentVersionId;


public class GetContentRpcHandler
    extends AbstractDataRpcHandler
{

    public GetContentRpcHandler()
    {
        super( "content_get" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final String path = context.param( "path" ).asString();
        final String[] stringOfPathIds = context.param( "contentIds" ).asStringArray();

        if ( StringUtils.isNotEmpty( path ) )
        {
            final ContentPath contentPath = ContentPath.from( path );
            handleGetContentByPath( contentPath, context );
        }
        else
        {
            handleGetContentByIds( ContentIds.from( stringOfPathIds ), context );
        }
    }

    private void handleGetContentByPath( final ContentPath contentPath, final JsonRpcContext context )
    {
        final Integer version = context.param( "version" ).asInteger();
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
            context.setResult( new GetContentJsonResult( content ) );
        }
        else
        {
            context.setResult( new JsonErrorResult( "Content [{0}] was not found", contentPath ) );
        }
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

    private void handleGetContentByIds( final ContentIds ids, final JsonRpcContext context )
    {
        final GetContents getContents = Commands.content().get();
        getContents.selectors( ids );
        final Contents contents = client.execute( getContents );
        if ( contents.isNotEmpty() )
        {
            context.setResult( new GetContentJsonResult( contents ) );
        }
        else
        {
            context.setResult( new JsonErrorResult( "Content [{0}] was not found", ids ) );
        }
    }
}
