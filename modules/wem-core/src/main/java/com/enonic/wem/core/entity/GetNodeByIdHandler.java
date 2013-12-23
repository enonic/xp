package com.enonic.wem.core.entity;


import com.enonic.wem.api.command.entity.GetNodeById;
import com.enonic.wem.core.command.CommandHandler;

public class GetNodeByIdHandler
    extends CommandHandler<GetNodeById>
{
    @Override
    public void handle()
        throws Exception
    {
        command.setResult( new GetNodeByIdService( context.getJcrSession(), command ).execute() );
    }
}
