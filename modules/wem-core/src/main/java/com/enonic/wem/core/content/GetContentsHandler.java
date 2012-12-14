package com.enonic.wem.core.content;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.GetContents;
import com.enonic.wem.api.content.ContentSelectors;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.content.dao.ContentDao;

@Component
public class GetContentsHandler
    extends AbstractContentHandler<GetContents>
{
    @Autowired
    private ContentDao contentDao;

    public GetContentsHandler()
    {
        super( GetContents.class );
    }

    @Override
    public void handle( final CommandContext context, final GetContents command )
        throws Exception
    {
        final ContentSelectors selectors = command.getSelectors();
        final Contents result = findContents( selectors, context );
        command.setResult( result );
    }
}
