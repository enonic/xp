package com.enonic.xp.admin.impl.json.content.page.region;

import com.enonic.xp.admin.impl.json.content.page.DescriptorJson;
import com.enonic.xp.page.region.PartDescriptor;


public class PartDescriptorJson
    extends DescriptorJson
{
    private final boolean editable;

    private final boolean deletable;

    public PartDescriptorJson( final PartDescriptor descriptor )
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
