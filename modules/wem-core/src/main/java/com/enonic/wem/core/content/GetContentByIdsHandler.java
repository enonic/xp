package com.enonic.wem.core.content;

import com.enonic.wem.api.command.content.GetContentByIds;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.core.command.CommandHandler;


public class GetContentByIdsHandler
    extends CommandHandler<GetContentByIds>
{
    @Override
    public void handle()
        throws Exception
    {
        final Contents contents = new GetContentByIdsService( this.context.getJcrSession(), this.command ).execute();
        command.setResult( contents );
    }

}
