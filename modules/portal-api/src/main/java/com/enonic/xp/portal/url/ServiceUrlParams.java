package com.enonic.xp.portal.url;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Multimap;

public final class ServiceUrlParams
    extends AbstractUrlParams<ServiceUrlParams>
{
    private String module;

    private String service;

    public String getService()
    {
        return this.service;
    }

    public String getModule()
    {
        return this.module;
    }

    public ServiceUrlParams service( final String value )
    {
        this.service = Strings.emptyToNull( value );
        return this;
    }

    public ServiceUrlParams module( final String value )
    {
        this.module = Strings.emptyToNull( value );
        return this;
    }

    @Override
    public ServiceUrlParams setAsMap( final Multimap<String, String> map )
    {
        service( singleValue( map, "_service" ) );
        module( singleValue( map, "_module" ) );
        getParams().putAll( map );
        return this;
    }

    @Override
    protected void buildToString( final Objects.ToStringHelper helper )
    {
        super.buildToString( helper );
        helper.add( "service", this.service );
        helper.add( "module", this.module );
    }
}
