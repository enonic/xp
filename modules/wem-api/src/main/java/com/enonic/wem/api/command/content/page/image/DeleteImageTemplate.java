package com.enonic.wem.api.command.content.page.image;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.page.image.ImageTemplateKey;

public final class DeleteImageTemplate
    extends Command<Boolean>
{
    private ImageTemplateKey key;

    public DeleteImageTemplate key( final ImageTemplateKey value )
    {
        this.key = value;
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
