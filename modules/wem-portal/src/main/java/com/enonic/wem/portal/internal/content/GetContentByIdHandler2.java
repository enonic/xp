package com.enonic.wem.portal.internal.content;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.script.command.CommandHandler2;
import com.enonic.wem.script.command.CommandRequest;

public final class GetContentByIdHandler2
    implements CommandHandler2
{
    private final ContentService contentService;

    public GetContentByIdHandler2( final ContentService contentService )
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
