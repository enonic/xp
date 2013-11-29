package com.enonic.wem.api.command.content.page.layout;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.command.content.page.TemplateEditor;
import com.enonic.wem.api.content.page.layout.LayoutTemplate;
import com.enonic.wem.api.content.page.layout.LayoutTemplateKey;

public final class UpdateLayoutTemplate
    extends Command<Boolean>
{
    private LayoutTemplateKey key;

    private TemplateEditor<LayoutTemplate> editor;

    public UpdateLayoutTemplate key( final LayoutTemplateKey key )
    {
        this.key = key;
        return this;
    }

    public UpdateLayoutTemplate editor( final TemplateEditor<LayoutTemplate> editor )
    {
        this.editor = editor;
        return this;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( key, "key is required" );
    }

    public LayoutTemplateKey getKey()
    {
        return key;
    }

    public TemplateEditor<LayoutTemplate> getEditor()
    {
        return editor;
    }
}
