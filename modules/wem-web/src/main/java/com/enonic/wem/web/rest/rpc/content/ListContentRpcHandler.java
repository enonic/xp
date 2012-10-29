package com.enonic.wem.web.rest.rpc.content;


import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.GetChildContent;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

@Component
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
        final ContentPath parentPath = ContentPath.from( context.param( "parentPath" ).required().asString() );

        final GetChildContent getChildContent = Commands.content().getChildren();
        getChildContent.parentPath( parentPath );

        final Contents contents = client.execute( getChildContent );
        context.setResult( new ListContentJsonResult( contents ) );
    }
}
