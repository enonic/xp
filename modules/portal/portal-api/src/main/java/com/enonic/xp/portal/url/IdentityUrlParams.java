package com.enonic.xp.portal.url;

import java.util.Objects;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.security.IdProviderKey;

import static com.google.common.base.Strings.isNullOrEmpty;

@PublicApi
public final class IdentityUrlParams
    extends AbstractUrlParams<IdentityUrlParams>
{
    private IdProviderKey idProviderKey;

    private String idProviderFunction;

    private String redirectionUrl;

    private ContextPathType contextPathType;

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

    public ContextPathType getContextPathType()
    {
        return Objects.requireNonNullElse( this.contextPathType, ContextPathType.VHOST );
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

    public IdentityUrlParams contextPathType( final String value )
    {
        this.contextPathType = isNullOrEmpty( value ) ? null : ContextPathType.from( value );
        return this;
    }

    @Override
    public String toString()
    {
        final MoreObjects.ToStringHelper helper = MoreObjects.toStringHelper( this );
        helper.omitNullValues();
        helper.add( "type", this.getType() );
        helper.add( "params", this.getParams() );
        helper.add( "idProviderKey", this.idProviderKey );
        helper.add( "idProviderFunction", this.idProviderFunction );
        helper.add( "redirect", this.redirectionUrl );
        return helper.toString();
    }
}