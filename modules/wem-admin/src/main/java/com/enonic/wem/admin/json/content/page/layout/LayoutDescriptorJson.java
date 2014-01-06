package com.enonic.wem.admin.json.content.page.layout;

import com.enonic.wem.admin.json.content.page.DescriptorJson;
import com.enonic.wem.api.content.page.layout.LayoutDescriptor;


public class LayoutDescriptorJson
    extends DescriptorJson
{
    private final boolean editable;

    private final boolean deletable;

    public LayoutDescriptorJson( final LayoutDescriptor descriptor )
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
