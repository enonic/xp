package com.enonic.wem.core.content;

import com.enonic.wem.api.command.content.GetChildContent;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.core.command.CommandHandler;


public class GetChildContentHandler
    extends CommandHandler<GetChildContent>
{
    private final static ContentHasChildPopulator CONTENT_HAS_CHILD_POPULATOR = new ContentHasChildPopulator();

    @Override
    public void handle()
        throws Exception
    {
        final Contents contents = new GetChildContentService( this.context.getJcrSession(), this.command ).execute();

        command.setResult( CONTENT_HAS_CHILD_POPULATOR.populateHasChild( this.context.getJcrSession(), contents ) );
    }

}
