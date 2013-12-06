package com.enonic.wem.admin.json.content.page.part;

import com.enonic.wem.admin.json.content.page.BaseDescriptorJson;
import com.enonic.wem.api.content.page.part.PartDescriptor;


public class PartDescriptorJson
    extends BaseDescriptorJson
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
