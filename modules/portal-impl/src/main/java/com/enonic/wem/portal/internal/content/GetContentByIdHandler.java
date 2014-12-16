package com.enonic.wem.portal.internal.content;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.script.command.CommandHandler;
import com.enonic.wem.script.command.CommandRequest;

public final class GetContentByIdHandler
    implements CommandHandler
{
    private final ContentService contentService;

    public GetContentByIdHandler( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    @Override
    public String getName()
    {
        return "content.getById";
    }

    @Override
    public Object execute( final CommandRequest req )
    {
        final ContentId contentId = req.param( "id" ).required().value( ContentId.class );
        return this.contentService.getById( contentId );
    }
}
