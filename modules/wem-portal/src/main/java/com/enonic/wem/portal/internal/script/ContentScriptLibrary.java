package com.enonic.wem.portal.internal.script;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.FindContentByParentParams;
import com.enonic.wem.script.ScriptLibrary;

public final class ContentScriptLibrary
    implements ScriptLibrary
{
    private final ContentService contentService;

    public ContentScriptLibrary( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    @Override
    public String getName()
    {
        return "contentService";
    }

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

    @Override
    public ScriptLibrary getInstance()
    {
        return this;
    }
}
