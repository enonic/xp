package com.enonic.xp.portal.url;

import com.google.common.annotations.Beta;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.Multimap;

import com.enonic.xp.security.UserStoreKey;

@Beta
public final class IdentityUrlParams
    extends AbstractUrlParams<IdentityUrlParams>
{
    private UserStoreKey userStoreKey;

    private String idProviderFunction;

    private String redirectionUrl;

    public UserStoreKey getUserStoreKey()
    {
        return userStoreKey;
    }

    public String getIdProviderFunction()
    {
        return idProviderFunction;
    }

    public String getRedirectionUrl()
    {
        return redirectionUrl;
    }

    public IdentityUrlParams userStoreKey( final UserStoreKey value )
    {
        this.userStoreKey = value;
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
    public IdentityUrlParams setAsMap( final Multimap<String, String> map )
    {
        super.setAsMap( map );
        redirectionUrl( singleValue( map, "_redirect" ) );
        getParams().putAll( map );
        return this;
    }

    @Override
    protected void buildToString( final MoreObjects.ToStringHelper helper )
    {
        super.buildToString( helper );
        helper.add( "userStoreKey", this.userStoreKey );
        helper.add( "idProviderFunction", this.idProviderFunction );
        helper.add( "redirect", this.redirectionUrl );
    }
}
