package com.enonic.wem.api.command.content;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.ContentSelectors;
import com.enonic.wem.api.content.editor.ContentEditor;

public final class UpdateContents
    extends Command
{
    private ContentSelectors selectors;

    private ContentEditor editor;

    private UserKey modifier;

    public UpdateContents()
    {
    }

    public ContentSelectors getSelectors()
    {
        return this.selectors;
    }

    public ContentEditor getEditor()
    {
        return this.editor;
    }

    public UserKey getModifier()
    {
        return modifier;
    }

    public UpdateContents selectors( final ContentSelectors selectors )
    {
        this.selectors = selectors;
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
        Preconditions.checkNotNull( this.selectors, "Content selectors cannot be null" );
        Preconditions.checkNotNull( this.editor, "Editor cannot be null" );
    }
}
