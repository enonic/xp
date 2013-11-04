package com.enonic.wem.admin.rest.resource.module.json;


import com.enonic.wem.admin.json.module.ModuleSummaryJson;

public class CreateModuleResultJson
{
    private final ModuleSummaryJson moduleSummary;
//    private ErrorJson error;

    private final boolean created;

    private final String failure;

    public CreateModuleResultJson( final ModuleSummaryJson moduleSummary )
    {
        this.moduleSummary = moduleSummary;
        this.created = true;
        this.failure = null;
    }

    public CreateModuleResultJson( final Throwable e )
    {
        this.failure = e.getMessage();
        this.created = false;
        this.moduleSummary = null;
    }

    public boolean isCreated()
    {
        return created;
    }

    public ModuleSummaryJson getModule()
    {
        return moduleSummary;
    }

    public String getFailure()
    {
        return failure;
    }
}
