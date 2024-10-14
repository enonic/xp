package com.enonic.xp.portal.url;

import java.util.List;
import java.util.Objects;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Multimap;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class ApiUrlParams
    extends AbstractUrlParams<ApiUrlParams>
{
    private String application;

    private String api;

    private String path;

    private List<String> pathSegments;

    public String getApplication()
    {
        return application;
    }

    public String getApi()
    {
        return api;
    }

    public String getPath()
    {
        return path;
    }

    public List<String> getPathSegments()
    {
        return pathSegments;
    }

    public ApiUrlParams application( final String value )
    {
        this.application = value;
        return this;
    }

    public ApiUrlParams api( final String value )
    {
        this.api = Objects.requireNonNull( value );
        return this;
    }

    public ApiUrlParams path( final String value )
    {
        this.path = value;
        return this;
    }

    public ApiUrlParams pathSegments( final List<String> pathSegments )
    {
        this.pathSegments = pathSegments;
        return this;
    }

    @Override
    public ApiUrlParams setAsMap( final Multimap<String, String> map )
    {
        super.setAsMap( map );
        api( singleValue( map, "_api" ) );
        application( singleValue( map, "_application" ) );
        getParams().putAll( map );
        return this;
    }

    @Override
    protected void buildToString( final MoreObjects.ToStringHelper helper )
    {
        super.buildToString( helper );
        helper.add( "api", this.api );
        helper.add( "application", this.application );
        helper.add( "path", this.path );
        helper.add( "pathSegments", this.pathSegments );
    }
}
