package com.enonic.wem.api.command.content.page;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.PageTemplateKey;

public final class DeletePageTemplate
    extends Command<Boolean>
{
    private PageTemplateKey key;

    public DeletePageTemplate key( final PageTemplateKey value )
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
