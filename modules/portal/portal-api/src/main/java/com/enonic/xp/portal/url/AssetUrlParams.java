package com.enonic.xp.portal.url;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class AssetUrlParams
    extends AbstractUrlParams<AssetUrlParams>
{
    private String application;

    private String path;

    public String getPath()
    {
        return this.path;
    }

    public String getApplication()
    {
        return this.application;
    }

    public AssetUrlParams path( final String value )
    {
        this.path = Strings.emptyToNull( value );
        return this;
    }

    public AssetUrlParams application( final String value )
    {
        this.application = Strings.emptyToNull( value );
        return this;
    }

    @Override
    public String toString()
    {
        final MoreObjects.ToStringHelper helper = MoreObjects.toStringHelper( this );
        helper.omitNullValues();
        helper.add( "type", this.getType() );
        helper.add( "params", this.getParams() );
        helper.add( "path", this.path );
        helper.add( "application", this.application );
        return helper.toString();
    }
}
