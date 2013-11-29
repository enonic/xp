package com.enonic.wem.api.command.content.page;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.content.page.PartTemplate;

public class GetPartTemplateByKey
    extends Command<PartTemplate>
{
    private PageTemplateKey key;

    public GetPartTemplateByKey key( final PageTemplateKey key )
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
