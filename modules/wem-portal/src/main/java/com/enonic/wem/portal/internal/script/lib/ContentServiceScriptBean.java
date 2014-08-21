package com.enonic.wem.portal.internal.script.lib;


import javax.inject.Inject;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.FindContentByParentParams;

public final class ContentServiceScriptBean
{
    @Inject
    private ContentService contentService;

    public Contents getRootContent()
    {
        return contentService.findByParent( FindContentByParentParams.create().
            from( 0 ).
            size( 500 ).
            parentPath( null ).
            build(), ContentConstants.CONTEXT_STAGE ).getContents();
    }

    public Contents getChildContent( final String parentPath )
    {
        final FindContentByParentParams params = FindContentByParentParams.create().
            from( 0 ).
            size( 500 ).
            parentPath( ContentPath.from( parentPath ) ).
            build();

        return contentService.findByParent( params, ContentConstants.CONTEXT_STAGE ).getContents();
    }

    public Content getContentById( final String id )
    {
        return contentService.getById( ContentId.from( id ), ContentConstants.CONTEXT_STAGE );
    }
}
