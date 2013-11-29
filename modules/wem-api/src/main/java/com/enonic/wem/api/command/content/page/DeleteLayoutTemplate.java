package com.enonic.wem.api.command.content.page;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.layout.LayoutTemplateKey;

public final class DeleteLayoutTemplate
    extends Command<Boolean>
{
    private LayoutTemplateKey key;

    public DeleteLayoutTemplate key( final LayoutTemplateKey value )
    {
        this.key = value;
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
}
