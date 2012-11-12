package com.enonic.wem.api.command.content;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.api.content.editor.ContentEditor;

public final class UpdateContents
    extends Command
{
    private ContentPaths paths;

    private ContentEditor editor;

    private UserKey modifier;

    public ContentPaths getPaths()
    {
        return this.paths;
    }

    public ContentEditor getEditor()
    {
        return this.editor;
    }

    public UserKey getModifier()
    {
        return modifier;
    }

    public UpdateContents paths( final ContentPaths paths )
    {
        this.paths = paths;
        return this;
    }

    public UpdateContents editor( final ContentEditor editor )
    {
        this.editor = editor;
        return this;
    }

    public UpdateContents modifier( final UserKey modifier )
    {
        this.modifier = modifier;
        return this;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.paths, "Content paths cannot be null" );
        Preconditions.checkNotNull( this.editor, "Editor cannot be null" );
    }
}
