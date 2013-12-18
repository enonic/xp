package com.enonic.wem.core.content;

import com.enonic.wem.api.command.content.GetContentByPath;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.core.command.CommandHandler;


public class GetContentByPathHandler
    extends CommandHandler<GetContentByPath>
{
    @Override
    public void handle()
        throws Exception
    {
        final Content content = new GetContentByPathService( this.context.getJcrSession(), this.command ).execute();
        command.setResult( content );
    }
}
