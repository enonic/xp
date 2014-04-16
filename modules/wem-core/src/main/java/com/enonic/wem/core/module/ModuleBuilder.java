package com.enonic.wem.core.module;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleVersion;
import com.enonic.wem.api.schema.content.ContentTypeName;

public final class ModuleBuilder
{
    protected ModuleKey moduleKey;

    protected String displayName;

    protected String info;

    protected String url;

    protected String vendorName;

    protected String vendorUrl;

    protected ModuleVersion minSystemVersion;

    protected ModuleVersion maxSystemVersion;

    protected List<ModuleKey> moduleDependencies = Lists.newArrayList();

    protected Set<ContentTypeName> contentTypeDependencies = Sets.newHashSet();

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

    public ModuleBuilder minSystemVersion( final ModuleVersion minSystemVersion )
    {
        this.minSystemVersion = minSystemVersion;
        return this;
    }

    public ModuleBuilder maxSystemVersion( final ModuleVersion maxSystemVersion )
    {
        this.maxSystemVersion = maxSystemVersion;
        return this;
    }

    public ModuleBuilder addModuleDependency( final ModuleKey moduleDependency )
    {
        this.moduleDependencies.add( moduleDependency );
        return this;
    }

    public ModuleBuilder addModuleDependencies( final Iterable<ModuleKey> moduleDependencies )
    {
        Iterables.addAll( this.moduleDependencies, moduleDependencies );
        return this;
    }

    public ModuleBuilder addContentTypeDependency( final ContentTypeName contentTypeDependency )
    {
        this.contentTypeDependencies.add( contentTypeDependency );
        return this;
    }

    public ModuleBuilder addContentTypeDependencies( final Iterable<ContentTypeName> contentTypeDependencies )
    {
        Iterables.addAll( this.contentTypeDependencies, contentTypeDependencies );
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
