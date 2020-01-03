package com.enonic.xp.portal.url;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.Multimap;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class ServiceUrlParams
    extends AbstractUrlParams<ServiceUrlParams>
{
    private String application;

    private String service;

    public String getService()
    {
        return this.service;
    }

    public String getApplication()
    {
        return this.application;
    }

    public ServiceUrlParams service( final String value )
    {
        this.service = Strings.emptyToNull( value );
        return this;
    }

    public ServiceUrlParams application( final String value )
    {
        this.application = Strings.emptyToNull( value );
        return this;
    }

    @Override
    public ServiceUrlParams setAsMap( final Multimap<String, String> map )
    {
        super.setAsMap( map );
        service( singleValue( map, "_service" ) );
        application( singleValue( map, "_application" ) );
        getParams().putAll( map );
        return this;
    }

    @Override
    protected void buildToString( final MoreObjects.ToStringHelper helper )
    {
        super.buildToString( helper );
        helper.add( "service", this.service );
        helper.add( "application", this.application );
    }
}
