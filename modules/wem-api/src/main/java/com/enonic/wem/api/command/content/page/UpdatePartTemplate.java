package com.enonic.wem.api.command.content.page;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.part.PartTemplate;
import com.enonic.wem.api.content.page.part.PartTemplateKey;

public final class UpdatePartTemplate
    extends Command<Boolean>
{
    private PartTemplateKey key;

    private TemplateEditor<PartTemplate> editor;

    public UpdatePartTemplate key( final PartTemplateKey key )
    {
        this.key = key;
        return this;
    }

    public UpdatePartTemplate editor( final TemplateEditor<PartTemplate> editor )
    {
        this.editor = editor;
        return this;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( key, "key is required" );
    }

    public PartTemplateKey getKey()
    {
        return key;
    }

    public TemplateEditor<PartTemplate> getEditor()
    {
        return editor;
    }
}
