package com.enonic.wem.portal.content;

import com.google.inject.Singleton;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.portal.AbstractPortalService;
import com.enonic.wem.portal.dispatch.PortalRequestPath;

@Singleton
public class ContentServiceImpl
    extends AbstractPortalService
    implements ContentService
{
    public Content getContent( final PageRequest pageRequest )
    {
        final PortalRequestPath portalRequestPath = pageRequest.getPortalRequest().getPortalRequestPath();

        final ContentPath contentPath =
            ContentPath.newPath().elements( portalRequestPath.getElements() ).build();

        return findContent( contentPath );
    }

    private Content findContent( final ContentPath contentPath )
    {
        return client.execute( Commands.content().get().byPath( contentPath ) );
    }
}
