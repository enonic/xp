package com.enonic.wem.api.command.content.page;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.ImageTemplate;
import com.enonic.wem.api.content.page.ImageTemplateKey;

public class GetImageTemplateByKey
    extends Command<ImageTemplate>
{
    private ImageTemplateKey key;

    public GetImageTemplateByKey key( final ImageTemplateKey key )
    {
        this.key = key;
        return this;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( key, "key is required" );
    }

    public ImageTemplateKey getKey()
    {
        return key;
    }
}
