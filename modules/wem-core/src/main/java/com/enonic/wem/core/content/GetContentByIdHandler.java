package com.enonic.wem.core.content;

import com.enonic.wem.api.command.content.GetContentById;
import com.enonic.wem.core.command.CommandHandler;


public class GetContentByIdHandler
    extends CommandHandler<GetContentById>
{
    @Override
    public void handle()
        throws Exception
    {
        command.setResult( new GetContentByIdService( this.context.getJcrSession(), this.command ).execute() );
    }
}
