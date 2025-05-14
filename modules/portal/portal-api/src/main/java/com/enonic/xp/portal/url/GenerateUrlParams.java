package com.enonic.xp.portal.url;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class GenerateUrlParams
    extends AbstractUrlParams<GenerateUrlParams>
{
    private String path;

    public String getPath()
    {
        return path;
    }

    public GenerateUrlParams url( final String url )
    {
        this.path = Strings.emptyToNull( url );
        return this;
    }

    @Override
    public String toString()
    {
        final MoreObjects.ToStringHelper helper = MoreObjects.toStringHelper( this );
        helper.omitNullValues();
        helper.add( "type", this.getType() );
        helper.add( "params", this.getParams() );
        helper.add( "path", this.path );
        return helper.toString();
    }
}
