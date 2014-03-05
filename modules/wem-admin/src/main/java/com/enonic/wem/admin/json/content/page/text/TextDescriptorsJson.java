package com.enonic.wem.admin.json.content.page.text;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.content.page.text.TextDescriptor;
import com.enonic.wem.api.content.page.text.TextDescriptors;


@SuppressWarnings("UnusedDeclaration")
public class TextDescriptorsJson
{
    private final TextDescriptors descriptors;

    private final List<TextDescriptorJson> descriptorJsonList;

    public TextDescriptorsJson( final TextDescriptors descriptors )
    {
        this.descriptors = descriptors;
        ImmutableList.Builder<TextDescriptorJson> builder = new ImmutableList.Builder<>();
        for ( TextDescriptor descriptor : descriptors )
        {
            builder.add( new TextDescriptorJson( descriptor ) );
        }
        this.descriptorJsonList = builder.build();
    }

    public List<TextDescriptorJson> getDescriptors()
    {
        return descriptorJsonList;
    }
}
