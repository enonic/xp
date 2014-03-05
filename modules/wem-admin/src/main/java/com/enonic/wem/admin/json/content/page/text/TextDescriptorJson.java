package com.enonic.wem.admin.json.content.page.text;

import com.enonic.wem.admin.json.content.page.DescriptorJson;
import com.enonic.wem.api.content.page.text.TextDescriptor;

public class TextDescriptorJson
    extends DescriptorJson
{
    private final boolean editable;

    private final boolean deletable;

    public TextDescriptorJson( final TextDescriptor descriptor )
    {
        super( descriptor );
        this.editable = false;
        this.deletable = false;
    }

    @Override
    public boolean getEditable()
    {
        return editable;
    }

    @Override
    public boolean getDeletable()
    {
        return deletable;
    }
}
