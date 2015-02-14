package com.enonic.xp.module.impl;

import org.osgi.framework.Bundle;

import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleVersion;
import com.enonic.wem.api.schema.mixin.MixinNames;

final class ModuleBuilder
{
    private final ModuleImpl module;

    public ModuleBuilder()
    {
        this.module = new ModuleImpl();
    }

    public ModuleBuilder moduleKey( final ModuleKey moduleKey )
    {
        this.module.moduleKey = moduleKey;
        return this;
    }

    public ModuleBuilder moduleVersion( final ModuleVersion moduleVersion )
    {
        this.module.moduleVersion = moduleVersion;
        return this;
    }

    public ModuleBuilder displayName( final String displayName )
    {
        this.module.displayName = displayName;
        return this;
    }

    public ModuleBuilder url( final String url )
    {
        this.module.url = url;
        return this;
    }

    public ModuleBuilder vendorName( final String vendorName )
    {
        this.module.vendorName = vendorName;
        return this;
    }

    public ModuleBuilder vendorUrl( final String vendorUrl )
    {
        this.module.vendorUrl = vendorUrl;
        return this;
    }

    public ModuleBuilder config( final Form config )
    {
        this.module.config = config;
        return this;
    }

    public ModuleBuilder bundle( final Bundle bundle )
    {
        this.module.bundle = bundle;
        return this;
    }

    public ModuleBuilder metaSteps( final MixinNames metaStepMixins )
    {
        this.module.metaSteps = metaStepMixins;
        return this;
    }

    public Module build()
    {
        return this.module;
    }
}
