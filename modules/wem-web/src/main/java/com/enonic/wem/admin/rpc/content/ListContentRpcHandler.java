package com.enonic.wem.admin.rpc.content;


import com.enonic.wem.admin.jsonrpc.JsonRpcContext;
import com.enonic.wem.admin.rpc.AbstractDataRpcHandler;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.GetChildContent;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.Contents;


public final class ListContentRpcHandler
    extends AbstractDataRpcHandler
{
    public ListContentRpcHandler()
    {
        super( "content_list" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final Contents contents;

        if ( context.param( "path" ).isNull() )
        {
            contents = client.execute( Commands.content().getRoot() );
        }
        else
        {
            final ContentPath parentPath = ContentPath.from( context.param( "path" ).required().asString() );

            final GetChildContent getChildContent = Commands.content().getChildren();
            getChildContent.parentPath( parentPath );

            contents = client.execute( getChildContent );
        }
        context.setResult( new ListContentJsonResult( contents ) );
    }
}
