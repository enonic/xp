package com.enonic.xp.portal.url;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.Multimap;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class ComponentUrlParams
    extends AbstractUrlParams<ComponentUrlParams>
{
    private String id;

    private String path;

    private String component;

    public String getId()
    {
        return this.id;
    }

    public String getPath()
    {
        return this.path;
    }

    public String getComponent()
    {
        return this.component;
    }

    public ComponentUrlParams id( final String value )
    {
        this.id = Strings.emptyToNull( value );
        return this;
    }

    public ComponentUrlParams path( final String value )
    {
        this.path = Strings.emptyToNull( value );
        return this;
    }

    public ComponentUrlParams component( final String value )
    {
        this.component = Strings.emptyToNull( value );
        return this;
    }

    @Override
    public ComponentUrlParams setAsMap( final Multimap<String, String> map )
    {
        super.setAsMap( map );
        id( singleValue( map, "_id" ) );
        path( singleValue( map, "_path" ) );
        component( singleValue( map, "_component" ) );
        getParams().putAll( map );
        return this;
    }

    @Override
    protected void buildToString( final MoreObjects.ToStringHelper helper )
    {
        super.buildToString( helper );
        helper.add( "id", this.id );
        helper.add( "path", this.path );
        helper.add( "component", this.component );
    }
}
