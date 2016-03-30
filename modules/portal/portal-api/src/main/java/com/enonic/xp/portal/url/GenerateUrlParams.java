package com.enonic.xp.portal.url;

import com.google.common.annotations.Beta;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.Multimap;

@Beta
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
    public GenerateUrlParams setAsMap( final Multimap<String, String> map )
    {
        super.setAsMap( map );
        url( singleValue( map, "_path" ) );
        getParams().putAll( map );
        return this;
    }

    @Override
    protected void buildToString( final MoreObjects.ToStringHelper helper )
    {
        super.buildToString( helper );
        helper.add( "path", this.path );
    }
}
