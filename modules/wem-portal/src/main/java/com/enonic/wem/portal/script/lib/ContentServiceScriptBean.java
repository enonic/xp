package com.enonic.wem.portal.script.lib;


import javax.inject.Inject;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.GetContentByParentParams;

public final class ContentServiceScriptBean
{
    @Inject
    private ContentService contentService;

    public Contents getRootContent()
    {
        return contentService.getByParent( GetContentByParentParams.create().
            from( 0 ).
            size( 500 ).
            parentPath( null ).
            build(), ContentConstants.CONTEXT_STAGE );
    }

    public Contents getChildContent( final String parentPath )
    {
        final GetContentByParentParams params = GetContentByParentParams.create().
            from( 0 ).
            size( 500 ).
            parentPath( ContentPath.from( parentPath ) ).
            build();

        return contentService.getByParent( params, ContentConstants.CONTEXT_STAGE );
    }

    public Content getContentById( final String id )
    {
        return contentService.getById( ContentId.from( id ), ContentConstants.CONTEXT_STAGE );
    }
}
