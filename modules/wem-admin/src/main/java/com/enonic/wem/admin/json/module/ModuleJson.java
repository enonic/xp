package com.enonic.wem.admin.json.module;

import com.enonic.wem.admin.json.form.FormJson;
import com.enonic.wem.api.module.Module;

public class ModuleJson
    extends ModuleSummaryJson
{
    private final FormJson config;

    public ModuleJson( final Module module )
    {
        super( module );
        this.config = module.getConfig() != null ? new FormJson( module.getConfig() ) : null;
    }

    public FormJson getConfig()
    {
        return config;
    }
}
