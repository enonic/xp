package com.enonic.wem.api.command.content.page.part;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.part.PartTemplate;
import com.enonic.wem.api.content.page.part.PartTemplateKey;

public class GetPartTemplateByKey
    extends Command<PartTemplate>
{
    private PartTemplateKey key;

    public GetPartTemplateByKey key( final PartTemplateKey key )
    {
        this.key = key;
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
