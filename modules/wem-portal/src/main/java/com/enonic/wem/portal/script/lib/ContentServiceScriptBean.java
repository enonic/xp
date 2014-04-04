package com.enonic.wem.portal.script.lib;


import javax.inject.Inject;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.GetChildContent;
import com.enonic.wem.api.command.content.GetContentById;
import com.enonic.wem.api.command.content.GetRootContent;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.Contents;

public final class ContentServiceScriptBean
{
    @Inject
    private Client client;

    public Contents getRootContent()
    {
        return client.execute( new GetRootContent() );
    }

    public Contents getChildContent( final String parentPath )
    {
        return client.execute( new GetChildContent().parentPath( ContentPath.from( parentPath ) ) );
    }

    public Content getContentById( final String id )
    {
        return client.execute( new GetContentById( ContentId.from( id ) ) );
    }
}
