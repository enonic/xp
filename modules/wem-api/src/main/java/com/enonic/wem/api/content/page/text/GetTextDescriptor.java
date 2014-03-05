package com.enonic.wem.api.content.page.text;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;

public class GetTextDescriptor
    extends Command<TextDescriptor>
{
    private final TextDescriptorKey key;

    public GetTextDescriptor( final TextDescriptorKey key )
    {
        this.key = key;
    }

    public TextDescriptorKey getKey()
    {
        return key;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( key, "key is required" );
    }
}
