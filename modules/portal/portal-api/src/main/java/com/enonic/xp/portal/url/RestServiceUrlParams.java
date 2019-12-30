package com.enonic.xp.portal.url;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.Multimap;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class RestServiceUrlParams
    extends AbstractUrlParams<RestServiceUrlParams>
{
    private String path;

    public String getPath()
    {
        return this.path;
    }

    public RestServiceUrlParams path( final String value )
    {
        this.path = Strings.emptyToNull( value );
        return this;
    }

    @Override
    public RestServiceUrlParams setAsMap( final Multimap<String, String> map )
    {
        path( singleValue( map, "_path" ) );
        return this;
    }

    @Override
    protected void buildToString( final MoreObjects.ToStringHelper helper )
    {
        super.buildToString( helper );
        helper.add( "path", this.path );
    }
}
