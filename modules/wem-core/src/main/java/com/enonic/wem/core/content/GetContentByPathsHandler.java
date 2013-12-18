package com.enonic.wem.core.content;

import com.enonic.wem.api.command.content.GetContentByPaths;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.core.command.CommandHandler;


public class GetContentByPathsHandler
    extends CommandHandler<GetContentByPaths>
{
    @Override
    public void handle()
        throws Exception
    {
        final Contents contents = new GetContentByPathsService( this.context.getJcrSession(), this.command ).execute();

        command.setResult( contents );
    }
}
