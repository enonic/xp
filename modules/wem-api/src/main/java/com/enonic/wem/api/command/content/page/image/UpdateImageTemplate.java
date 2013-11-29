package com.enonic.wem.api.command.content.page.image;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.image.ImageTemplate;
import com.enonic.wem.api.content.page.image.ImageTemplateEditor;
import com.enonic.wem.api.content.page.image.ImageTemplateKey;

public final class UpdateImageTemplate
    extends Command<ImageTemplate>
{
    private ImageTemplateKey key;

    private ImageTemplateEditor editor;

    public UpdateImageTemplate key( final ImageTemplateKey key )
    {
        this.key = key;
        return this;
    }

    public UpdateImageTemplate editor( final ImageTemplateEditor editor )
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

    public ImageTemplateEditor getEditor()
    {
        return editor;
    }
}
