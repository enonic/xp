package com.enonic.wem.web.rest.rpc.content;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.GetContentVersion;
import com.enonic.wem.api.command.content.GetContents;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.versioning.ContentVersionId;
import com.enonic.wem.web.json.JsonErrorResult;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

@Component
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
        final String path = context.param( "path" ).required().asString();
        final Integer version = context.param( "version" ).asInteger();
        final Content content;
        if ( version == null )
        {
            content = findContent( ContentPath.from( path ) );
        }
        else
        {
            content = findContentVersion( ContentPath.from( path ), ContentVersionId.of( version ) );
        }

        if ( content != null )
        {
            context.setResult( new GetContentJsonResult( content ) );
        }
        else
        {
            context.setResult( new JsonErrorResult( "Content [{0}] was not found", path ) );
        }
    }

    private Content findContent( final ContentPath contentPath )
    {
        final GetContents getContent = Commands.content().get();
        getContent.selectors( ContentPaths.from( contentPath ) );

        final Contents contents = client.execute( getContent );
        return contents.isNotEmpty() ? contents.first() : null;
    }

    private Content findContentVersion( final ContentPath contentPath, final ContentVersionId versionId )
    {
        final GetContentVersion getContentVersion = Commands.content().getVersion();
        getContentVersion.selector( contentPath ).version( versionId );
        return client.execute( getContentVersion );
    }
}
