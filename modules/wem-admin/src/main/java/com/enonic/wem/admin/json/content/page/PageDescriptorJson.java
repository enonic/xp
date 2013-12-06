package com.enonic.wem.admin.json.content.page;

import com.enonic.wem.api.content.page.PageDescriptor;


public class PageDescriptorJson
    extends BaseDescriptorJson
{
    private final boolean editable;

    private final boolean deletable;

    public PageDescriptorJson( final PageDescriptor descriptor )
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
