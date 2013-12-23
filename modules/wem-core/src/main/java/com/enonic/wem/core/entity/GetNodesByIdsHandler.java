package com.enonic.wem.core.entity;


import com.enonic.wem.api.command.entity.GetNodesByIds;
import com.enonic.wem.core.command.CommandHandler;

public class GetNodesByIdsHandler
    extends CommandHandler<GetNodesByIds>
{
    @Override
    public void handle()
        throws Exception
    {
        command.setResult( new GetNodesByIdsService( context.getJcrSession(), this.command ).execute() );
    }
}
