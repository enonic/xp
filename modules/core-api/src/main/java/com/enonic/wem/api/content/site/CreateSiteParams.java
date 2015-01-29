package com.enonic.wem.api.content.site;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.ContentName;
import com.enonic.wem.api.content.ContentPath;

public final class CreateSiteParams
{
    private ContentPath parentContentPath;

    private ContentName name;

    private String displayName;

    private String description;

    private ModuleConfigs moduleConfigs;

    private boolean requireValid = true;

    public CreateSiteParams parent( final ContentPath parentContentPath )
    {
        this.parentContentPath = parentContentPath;
        Preconditions.checkArgument( parentContentPath.isAbsolute(), "parentContentPath must be absolute: " + parentContentPath );
        return this;
    }

    public CreateSiteParams name( final String name )
    {
        this.name = ContentName.from( name );
        return this;
    }

    public CreateSiteParams name( final ContentName name )
    {
        this.name = name;
        return this;
    }

    public CreateSiteParams displayName( final String displayName )
    {
        this.displayName = displayName;
        return this;
    }

    public CreateSiteParams description( final String value )
    {
        this.description = value;
        return this;
    }

    public CreateSiteParams moduleConfigs( final ModuleConfigs value )
    {
        this.moduleConfigs = value;
        return this;
    }

    public CreateSiteParams requireValid()
    {
        this.requireValid = true;
        return this;
    }

    public ContentPath getParentContentPath()
    {
        return parentContentPath;
    }

    public ContentName getName()
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

    public ModuleConfigs getModuleConfigs()
    {
        return moduleConfigs;
    }

    public boolean isRequireValid()
    {
        return requireValid;
    }
}
