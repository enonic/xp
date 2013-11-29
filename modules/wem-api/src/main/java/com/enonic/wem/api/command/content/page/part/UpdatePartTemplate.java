package com.enonic.wem.api.command.content.page.part;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.part.PartTemplate;
import com.enonic.wem.api.content.page.part.PartTemplateEditor;
import com.enonic.wem.api.content.page.part.PartTemplateKey;

public final class UpdatePartTemplate
    extends Command<PartTemplate>
{
    private PartTemplateKey key;

    private PartTemplateEditor editor;

    public UpdatePartTemplate key( final PartTemplateKey key )
    {
        this.key = key;
        return this;
    }

    public UpdatePartTemplate editor( final PartTemplateEditor editor )
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

    public PartTemplateEditor getEditor()
    {
        return editor;
    }
}
