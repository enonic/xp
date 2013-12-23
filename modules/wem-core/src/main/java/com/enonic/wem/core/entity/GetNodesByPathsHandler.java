package com.enonic.wem.core.entity;


import com.enonic.wem.api.command.entity.GetNodesByPaths;
import com.enonic.wem.core.command.CommandHandler;

public class GetNodesByPathsHandler
    extends CommandHandler<GetNodesByPaths>
{
    @Override
    public void handle()
    {
        command.setResult( new GetNodesByPathsService( context.getJcrSession(), this.command ).execute() );
    }
}
