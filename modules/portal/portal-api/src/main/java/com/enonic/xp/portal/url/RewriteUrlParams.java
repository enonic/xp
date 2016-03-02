package com.enonic.xp.portal.url;

import com.google.common.annotations.Beta;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.Multimap;

@Beta
public final class RewriteUrlParams
    extends AbstractUrlParams<RewriteUrlParams>
{
    private String url;

    public String getUrl()
    {
        return url;
    }

    public RewriteUrlParams url( final String url )
    {
        this.url = Strings.emptyToNull( url );
        return this;
    }

    @Override
    public RewriteUrlParams setAsMap( final Multimap<String, String> map )
    {
        super.setAsMap( map );
        url( singleValue( map, "_url" ) );
        getParams().putAll( map );
        return this;
    }

    @Override
    protected void buildToString( final MoreObjects.ToStringHelper helper )
    {
        super.buildToString( helper );
        helper.add( "url", this.url );
    }
}
