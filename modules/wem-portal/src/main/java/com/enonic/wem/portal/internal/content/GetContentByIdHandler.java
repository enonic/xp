package com.enonic.wem.portal.internal.content;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.portal.content.GetContentById;
import com.enonic.wem.script.command.CommandHandler;

public final class GetContentByIdHandler
    implements CommandHandler<GetContentById>
{
    private final ContentService contentService;

    public GetContentByIdHandler( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    @Override
    public Class<GetContentById> getType()
    {
        return GetContentById.class;
    }

    @Override
    public GetContentById newCommand()
    {
        return new GetContentById();
    }

    @Override
    public void invoke( final GetContentById command )
    {
        final ContentId contentId = ContentId.from( command.getId() );
        final Content content = contentService.getById( contentId );
        command.setResult( content );
    }
}
