package com.enonic.wem.core.content;

import com.enonic.wem.api.command.content.GetRootContent;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.core.command.CommandHandler;

public class GetRootContentHandler
    extends CommandHandler<GetRootContent>
{
    @Override
    public void handle()
        throws Exception
    {
        final Contents contents = new GetRootContentService( this.context.getJcrSession(), this.command ).execute();

        command.setResult( contents );
    }
}
