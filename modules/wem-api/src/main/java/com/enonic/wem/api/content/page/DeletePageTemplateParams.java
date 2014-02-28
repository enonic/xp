package com.enonic.wem.api.content.page;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;

public final class DeletePageTemplateParams
    extends Command<Boolean>
{
    private PageTemplateKey key;

    public DeletePageTemplateParams key( final PageTemplateKey value )
    {
        this.key = value;
        return this;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( key, "key is required" );
    }

    public PageTemplateKey getKey()
    {
        return key;
    }
}
