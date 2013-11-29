package com.enonic.wem.api.command.content.page.image;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.command.content.page.TemplateEditor;
import com.enonic.wem.api.content.page.image.ImageTemplate;
import com.enonic.wem.api.content.page.image.ImageTemplateKey;

public final class UpdateImageTemplate
    extends Command<Boolean>
{
    private ImageTemplateKey key;

    private TemplateEditor<ImageTemplate> editor;

    public UpdateImageTemplate key( final ImageTemplateKey key )
    {
        this.key = key;
        return this;
    }

    public UpdateImageTemplate editor( final TemplateEditor<ImageTemplate> editor )
    {
        this.editor = editor;
        return this;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( key, "key is required" );
    }

    public ImageTemplateKey getKey()
    {
        return key;
    }

    public TemplateEditor<ImageTemplate> getEditor()
    {
        return editor;
    }
}
