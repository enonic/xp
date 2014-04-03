package com.enonic.wem.core.content;

import com.enonic.wem.api.command.content.GetContentByPath;
import com.enonic.wem.api.command.schema.content.ContentTypeService;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.entity.GetNodeByPathParams;
import com.enonic.wem.api.entity.NoNodeAtPathFoundException;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.NodeService;
import com.enonic.wem.core.command.CommandContext;


public class GetContentByPathService
    extends ContentService
{
    private final GetContentByPath command;

    public GetContentByPathService( final CommandContext context,
                                    final GetContentByPath command,
                                    final NodeService nodeService,
                                    final ContentTypeService contentTypeService )
    {
        super( context, nodeService, contentTypeService );
        this.command = command;
    }

    public Content execute()
        throws Exception
    {
        final NodePath nodePath = ContentNodeHelper.translateContentPathToNodePath( command.getPath() );

        try
        {
            final Node node = nodeService.getByPath( new GetNodeByPathParams( nodePath ) );
            return translator.fromNode( node );
        }
        catch ( NoNodeAtPathFoundException e )
        {
            throw new ContentNotFoundException( command.getPath() );

        }
    }
}
