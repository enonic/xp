package com.enonic.wem.admin.json.module;

import com.enonic.wem.admin.json.ItemJson;
import com.enonic.wem.api.module.Module;

public class ModuleSummaryJson
    implements ItemJson
{
    private final Module module;

    private final boolean editable;

    private final boolean deletable;

    public ModuleSummaryJson( final Module module )
    {
        this.module = module;
        this.editable = true;
        this.deletable = true;
    }

    public String getKey()
    {
        return module.getModuleKey().toString();
    }

    public String getName()
    {
        return module.getName().toString();
    }

    public String getVersion()
    {
        return module.getVersion().toString();
    }

    public String getDisplayName()
    {
        return module.getDisplayName();
    }

    public String getInfo()
    {
        return module.getInfo();
    }

    public String getUrl()
    {
        return module.getUrl();
    }

    public String getVendorName()
    {
        return module.getVendorName();
    }

    public String getVendorUrl()
    {
        return module.getVendorUrl();
    }

    @Override
    public boolean getDeletable()
    {
        return deletable;
    }

    @Override
    public boolean getEditable()
    {
        return editable;
    }

}
