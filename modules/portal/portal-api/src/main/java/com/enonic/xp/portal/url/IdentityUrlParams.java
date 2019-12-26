package com.enonic.xp.portal.url;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.Multimap;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.security.IdProviderKey;

@PublicApi
public final class IdentityUrlParams
    extends AbstractUrlParams<IdentityUrlParams>
{
    private IdProviderKey idProviderKey;

    private String idProviderFunction;

    private String redirectionUrl;

    public IdProviderKey getIdProviderKey()
    {
        return idProviderKey;
    }

    public String getIdProviderFunction()
    {
        return idProviderFunction;
    }

    public String getRedirectionUrl()
    {
        return redirectionUrl;
    }

    public IdentityUrlParams idProviderKey( final IdProviderKey value )
    {
        this.idProviderKey = value;
        return this;
    }

    public IdentityUrlParams idProviderFunction( final String value )
    {
        this.idProviderFunction = Strings.emptyToNull( value );
        return this;
    }

    public IdentityUrlParams redirectionUrl( final String value )
    {
        this.redirectionUrl = Strings.emptyToNull( value );
        return this;
    }

    @Override
    protected ContextPathType getDefaultContextPath()
    {
        return ContextPathType.VHOST;
    }

    @Override
    public IdentityUrlParams setAsMap( final Multimap<String, String> map )
    {
        super.setAsMap( map );

        redirectionUrl( singleValue( map, "_redirect" ) );
        final String idProviderKey = singleValue( map, "_idProvider" );
        if ( idProviderKey != null )
        {
            idProviderKey( IdProviderKey.from( idProviderKey ) );
        }
        getParams().putAll( map );
        return this;
    }

    @Override
    protected void buildToString( final MoreObjects.ToStringHelper helper )
    {
        super.buildToString( helper );
        helper.add( "idProviderKey", this.idProviderKey );
        helper.add( "idProviderFunction", this.idProviderFunction );
        helper.add( "redirect", this.redirectionUrl );
    }
}