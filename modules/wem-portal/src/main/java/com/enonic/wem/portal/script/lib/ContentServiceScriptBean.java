package com.enonic.wem.portal.script.lib;


import javax.inject.Inject;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.Contents;

public final class ContentServiceScriptBean
{
    @Inject
    private ContentService contentService;

    public Contents getRootContent()
    {
        return contentService.getRoots( ContentConstants.DEFAULT_CONTEXT );
    }

    public Contents getChildContent( final String parentPath )
    {
        return contentService.getChildren( ContentPath.from( parentPath ), ContentConstants.DEFAULT_CONTEXT );
    }

    public Content getContentById( final String id )
    {
        return contentService.getById( ContentId.from( id ), ContentConstants.DEFAULT_CONTEXT );
    }
}
