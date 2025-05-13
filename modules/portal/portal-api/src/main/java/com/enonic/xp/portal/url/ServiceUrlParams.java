package com.enonic.xp.portal.url;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;

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
    public String toString()
    {
        final MoreObjects.ToStringHelper helper = MoreObjects.toStringHelper( this );
        helper.omitNullValues();
        helper.add( "type", this.getType() );
        helper.add( "params", this.getParams() );
        helper.add( "service", this.service );
        helper.add( "application", this.application );
        return helper.toString();
    }
}
