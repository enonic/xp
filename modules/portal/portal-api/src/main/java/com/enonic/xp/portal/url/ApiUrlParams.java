package com.enonic.xp.portal.url;

import java.util.Objects;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.Multimap;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.script.ScriptValue;

@PublicApi
public final class ApiUrlParams
    extends AbstractUrlParams<ApiUrlParams>
{
    private String application;

    private String api;

    private Object path;

    public String getApplication()
    {
        return application;
    }

    public String getApi()
    {
        return api;
    }

    public Object getPath()
    {
        return path;
    }

    public ApiUrlParams application( final String value )
    {
        this.application = Objects.requireNonNull( value );
        return this;
    }

    public ApiUrlParams api( final String value )
    {
        this.api = Strings.emptyToNull( value );
        return this;
    }

    public ApiUrlParams path( final Object value )
    {
        if ( value instanceof ScriptValue && ( (ScriptValue) value ).isArray() )
        {
            this.path = ( (ScriptValue) value ).getArray( String.class );
        }
        else
        {
            this.path = value;
        }
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
    }
}
