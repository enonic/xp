package com.enonic.wem.core.content;

import javax.jcr.Session;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.GetContentVersion;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentSelector;
import com.enonic.wem.api.content.versioning.ContentVersionId;
import com.enonic.wem.core.command.CommandContext;

@Component
public class GetContentVersionHandler
    extends AbstractContentHandler<GetContentVersion>
{
    public GetContentVersionHandler()
    {
        super( GetContentVersion.class );
    }

    @Override
    public void handle( final CommandContext context, final GetContentVersion command )
        throws Exception
    {
        final ContentSelector selector = command.getSelector();
        final ContentVersionId versionId = command.getVersion();
        final Content content = getContentVersion( selector, versionId, context.getJcrSession() );
        command.setResult( content );
    }

    private Content getContentVersion( final ContentSelector selector, final ContentVersionId versionId, final Session session )
    {
        final Content content;
        if ( selector instanceof ContentPath )
        {
            final ContentPath path = (ContentPath) selector;
            content = contentDao.getContentVersion( path, versionId, session );
        }
        else if ( selector instanceof ContentId )
        {
            final ContentId contentId = (ContentId) selector;
            content = contentDao.getContentVersion( contentId, versionId, session );
        }
        else
        {
            throw new IllegalArgumentException( "Unsupported content selector: " + selector.getClass().getCanonicalName() );
        }
        return content;
    }

}
