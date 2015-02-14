package com.enonic.xp.portal.impl.xslt.function;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.url.PageUrlParams;

final class PageUrlFunction
    extends AbstractUrlFunction
{
    public PageUrlFunction()
    {
        super( "pageUrl" );
    }

    @Override
    protected String execute( final Multimap<String, String> map )
    {
        final PageUrlParams params = new PageUrlParams().context( getContext() ).setAsMap( map );
        return this.urlService.pageUrl( params );
    }
}
