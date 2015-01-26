package com.enonic.xp.portal.url;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Multimap;

import com.enonic.wem.api.module.ModuleKey;

public final class AssetUrlParams
    extends AbstractUrlParams<AssetUrlParams>
{
    private ModuleKey module;

    private String path;

    public String getPath()
    {
        return this.path;
    }

    public ModuleKey getModule()
    {
        return this.module;
    }

    public AssetUrlParams path( final String value )
    {
        this.path = Strings.emptyToNull( value );
        return this;
    }

    public AssetUrlParams module( final String value )
    {
        return Strings.isNullOrEmpty( value ) ? this : module( ModuleKey.from( value ) );
    }

    public AssetUrlParams module( final ModuleKey value )
    {
        this.module = value;
        return this;
    }

    @Override
    public AssetUrlParams setAsMap( final Multimap<String, String> map )
    {
        path( singleValue( map, "_path" ) );
        module( singleValue( map, "_module" ) );
        getParams().putAll( map );
        return this;
    }

    @Override
    protected void buildToString( final Objects.ToStringHelper helper )
    {
        super.buildToString( helper );
        helper.add( "path", this.path );
        helper.add( "module", this.module );
    }
}
