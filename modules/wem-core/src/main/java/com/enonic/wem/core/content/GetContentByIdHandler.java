package com.enonic.wem.core.content;

import com.enonic.wem.api.command.content.GetContentById;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.core.command.CommandHandler;


public class GetContentByIdHandler
    extends CommandHandler<GetContentById>
{
    @Override
    public void handle()
        throws Exception
    {
        final Content content = new GetContentByIdService( this.context.getJcrSession(), this.command ).execute();

        command.setResult( content );
    }
}
