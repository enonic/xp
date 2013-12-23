package com.enonic.wem.core.content;

import com.enonic.wem.api.command.content.GetChildContent;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.core.command.CommandHandler;


public class GetChildContentHandler
    extends CommandHandler<GetChildContent>
{
    @Override
    public void handle()
        throws Exception
    {
        final Contents contents =
            new GetChildContentService( this.context.getJcrSession(), this.command ).populateChildIds( true ).execute();

        command.setResult( contents );
    }

}
