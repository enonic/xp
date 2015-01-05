package com.enonic.wem.admin.json.content.page.region;

import com.enonic.wem.admin.json.content.page.DescriptorJson;
import com.enonic.wem.api.content.page.region.PartDescriptor;


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
