package com.enonic.xp.portal.url;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.Multimap;

public final class AssetUrlParams
    extends AbstractUrlParams<AssetUrlParams>
{
    private String module;

    private String path;

    public String getPath()
    {
        return this.path;
    }

    public String getModule()
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
        this.module = Strings.emptyToNull( value );
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
    protected void buildToString( final MoreObjects.ToStringHelper helper )
    {
        super.buildToString( helper );
        helper.add( "path", this.path );
        helper.add( "module", this.module );
    }
}
