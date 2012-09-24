package com.enonic.wem.api.command.content;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.ContentKeys;
import com.enonic.wem.api.content.editor.ContentEditor;

public final class UpdateContents
    extends Command
{

    private ContentKeys keys;

    private ContentEditor editor;

    public ContentKeys getKeys()
    {
        return this.keys;
    }

    public ContentEditor getEditor()
    {
        return this.editor;
    }

    public UpdateContents keys( final ContentKeys keys )
    {
        this.keys = keys;
        return this;
    }

    public UpdateContents editor( final ContentEditor editor )
    {
        this.editor = editor;
        return this;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.keys, "Content keys cannot be null" );
        Preconditions.checkNotNull( this.editor, "Editor cannot be null" );
    }
}
