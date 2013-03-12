package com.enonic.wem.api.command.content;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.ContentSelector;
import com.enonic.wem.api.content.editor.ContentEditor;

public final class UpdateContent
    extends Command
{
    private ContentSelector selector;

    private ContentEditor editor;

    private UserKey modifier;

    public UpdateContent()
    {
    }

    public ContentSelector getSelector()
    {
        return this.selector;
    }

    public ContentEditor getEditor()
    {
        return this.editor;
    }

    public UserKey getModifier()
    {
        return modifier;
    }

    public UpdateContent selector( final ContentSelector selectors )
    {
        this.selector = selectors;
        return this;
    }

    public UpdateContent editor( final ContentEditor editor )
    {
        this.editor = editor;
        return this;
    }

    public UpdateContent modifier( final UserKey modifier )
    {
        this.modifier = modifier;
        return this;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.selector, "selector cannot be null" );
        Preconditions.checkNotNull( this.editor, "editor cannot be null" );
    }
}
