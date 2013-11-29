package com.enonic.wem.api.command.content.page;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.layout.LayoutTemplate;
import com.enonic.wem.api.content.page.layout.LayoutTemplateKey;

public class GetLayoutTemplateByKey
    extends Command<LayoutTemplate>
{
    private LayoutTemplateKey key;

    public GetLayoutTemplateByKey key( final LayoutTemplateKey key )
    {
        this.key = key;
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
