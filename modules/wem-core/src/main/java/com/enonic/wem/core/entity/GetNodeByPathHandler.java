package com.enonic.wem.core.entity;


import com.enonic.wem.api.command.entity.GetNodeByPath;
import com.enonic.wem.core.command.CommandHandler;

public class GetNodeByPathHandler
    extends CommandHandler<GetNodeByPath>
{

    @Override
    public void handle()
    {
        command.setResult( new GetNodeByPathService( context.getJcrSession(), command ).execute() );
    }
}
