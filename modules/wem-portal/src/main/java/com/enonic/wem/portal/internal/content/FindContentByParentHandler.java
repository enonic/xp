package com.enonic.wem.portal.internal.content;

import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.FindContentByParentParams;
import com.enonic.wem.portal.content.FindContentByParent;
import com.enonic.wem.script.command.CommandHandler;

public final class FindContentByParentHandler
    implements CommandHandler<FindContentByParent>
{
    private final ContentService contentService;

    public FindContentByParentHandler( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    @Override
    public Class<FindContentByParent> getType()
    {
        return FindContentByParent.class;
    }

    @Override
    public FindContentByParent newCommand()
    {
        return new FindContentByParent();
    }

    @Override
    public void invoke( final FindContentByParent command )
    {
        final FindContentByParentParams params = FindContentByParentParams.create().
            from( command.getFrom() ).
            size( command.getSize() ).
            parentPath( ContentPath.from( command.getParentPath() ) ).
            build();

        final Contents result = contentService.findByParent( params ).getContents();
        command.setResult( result );
    }
}
