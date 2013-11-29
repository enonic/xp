package com.enonic.wem.api.command.content.page.part;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.part.PartTemplateKey;

public final class DeletePartTemplate
    extends Command<Boolean>
{
    private PartTemplateKey key;

    public DeletePartTemplate key( final PartTemplateKey value )
    {
        this.key = value;
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
}
