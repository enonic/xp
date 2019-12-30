package com.enonic.xp.portal.url;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.Multimap;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class ProcessHtmlParams
    extends AbstractUrlParams<ProcessHtmlParams>
{
    private String value;

    public String getValue()
    {
        return this.value;
    }

    public ProcessHtmlParams value( final String value )
    {
        this.value = Strings.emptyToNull( value );
        return this;
    }

    @Override
    public ProcessHtmlParams setAsMap( final Multimap<String, String> map )
    {
        super.setAsMap( map );
        value( singleValue( map, "_value" ) );
        getParams().putAll( map );
        return this;
    }

    @Override
    protected void buildToString( final MoreObjects.ToStringHelper helper )
    {
        super.buildToString( helper );
        helper.add( "value", this.value );
    }
}
