package com.enonic.wem.core.content;

import com.enonic.wem.api.command.content.GetContentByPath;
import com.enonic.wem.core.command.CommandHandler;


public class GetContentByPathHandler
    extends CommandHandler<GetContentByPath>
{
    @Override
    public void handle()
        throws Exception
    {
        command.setResult( new GetContentByPathService( this.context.getJcrSession(), this.command ).execute() );
    }
}
