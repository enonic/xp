package com.enonic.wem.admin.json.content.page.image;

import com.enonic.wem.admin.json.content.page.BaseDescriptorJson;
import com.enonic.wem.api.content.page.image.ImageDescriptor;


public class ImageDescriptorJson
    extends BaseDescriptorJson
{
    private final boolean editable;

    private final boolean deletable;

    public ImageDescriptorJson( final ImageDescriptor descriptor )
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
