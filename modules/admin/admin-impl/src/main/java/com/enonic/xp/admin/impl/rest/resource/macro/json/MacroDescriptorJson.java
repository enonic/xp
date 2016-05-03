package com.enonic.xp.admin.impl.rest.resource.macro.json;

import com.enonic.xp.admin.impl.json.form.FormJson;
import com.enonic.xp.macro.MacroDescriptor;

public class MacroDescriptorJson
{
    private String key;

    private String name;

    private String displayName;

    private String description;

    private FormJson form;

    private String iconUrl;

    public MacroDescriptorJson( final MacroDescriptor macroDescriptor )
    {
        this.key = macroDescriptor.getKey().toString();
        this.name = macroDescriptor.getName();
        this.displayName = macroDescriptor.getDisplayName();
        this.description = macroDescriptor.getDescription();
        this.form = new FormJson( macroDescriptor.getForm() );
    }

    public String getKey()
    {
        return key;
    }

    public String getName()
    {
        return name;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getDescription()
    {
        return description;
    }

    public FormJson getForm()
    {
        return form;
    }

    public String getIconUrl()
    {
        return iconUrl;
    }
}
