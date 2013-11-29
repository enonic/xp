package com.enonic.wem.api.command.content.page;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateKey;

public class GetPageTemplateByKey
    extends Command<PageTemplate>
{
    private PageTemplateKey key;

    public GetPageTemplateByKey key( final PageTemplateKey key )
    {
        this.key = key;
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
