package com.enonic.wem.core.module;

import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;

public final class ModuleBuilder
{
    protected ModuleKey moduleKey;

    protected String displayName;

    protected String info;

    protected String url;

    protected String vendorName;

    protected String vendorUrl;

    protected Form config;

    private ModuleBuilder()
    {
    }

    public static ModuleBuilder newModule()
    {
        return new ModuleBuilder();
    }

    public ModuleBuilder moduleKey( final ModuleKey moduleKey )
    {
        this.moduleKey = moduleKey;
        return this;
    }

    public ModuleBuilder displayName( final String displayName )
    {
        this.displayName = displayName;
        return this;
    }

    public ModuleBuilder info( final String info )
    {
        this.info = info;
        return this;
    }

    public ModuleBuilder url( final String url )
    {
        this.url = url;
        return this;
    }

    public ModuleBuilder vendorName( final String vendorName )
    {
        this.vendorName = vendorName;
        return this;
    }

    public ModuleBuilder vendorUrl( final String vendorUrl )
    {
        this.vendorUrl = vendorUrl;
        return this;
    }

    public ModuleBuilder config( final Form config )
    {
        this.config = config;
        return this;
    }

    public Module build()
    {
        return new ModuleImpl( this );
    }
}
