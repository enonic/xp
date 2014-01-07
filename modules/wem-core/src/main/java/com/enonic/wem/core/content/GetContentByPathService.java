package com.enonic.wem.core.content;

import com.enonic.wem.api.command.content.GetContentByPath;
import com.enonic.wem.api.command.entity.GetNodeByPath;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.entity.NoNodeAtPathFoundException;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.entity.GetNodeByPathService;


public class GetContentByPathService
    extends ContentService
{
    private final GetContentByPath command;

    public GetContentByPathService( final CommandContext context, final GetContentByPath command )
    {
        super( context );
        this.command = command;
    }

    public Content execute()
        throws Exception
    {
        final GetNodeByPath getNodeByPathCommand =
            new GetNodeByPath( ContentNodeHelper.translateContentPathToNodePath( command.getPath() ) );

        try
        {
            return translator.fromNode( new GetNodeByPathService( session, getNodeByPathCommand ).execute() );
        }
        catch ( NoNodeAtPathFoundException e )
        {
            throw new ContentNotFoundException( command.getPath() );

        }
    }
}
