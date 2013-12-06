package com.enonic.wem.admin.json.content.page;

import com.google.common.base.Preconditions;

import com.enonic.wem.admin.json.ItemJson;
import com.enonic.wem.admin.json.form.FormJson;
import com.enonic.wem.api.content.page.BaseDescriptor;


public abstract class BaseDescriptorJson
    implements ItemJson
{
    private final BaseDescriptor descriptor;

    private final FormJson configFormJson;

    public BaseDescriptorJson( final BaseDescriptor descriptor )
    {
        Preconditions.checkNotNull( descriptor );
        this.descriptor = descriptor;
        this.configFormJson = new FormJson( descriptor.getConfigForm() );
    }

    public String getName()
    {
        return descriptor.getName() != null ? descriptor.getName().toString() : null;
    }

    public String getDisplayName()
    {
        return descriptor.getDisplayName();
    }

    public String getController()
    {
        return descriptor.getControllerResource().toString();
    }

    public FormJson getConfigForm()
    {
        return configFormJson;
    }

}
