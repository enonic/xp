package com.enonic.xp.portal.xslt.impl.function;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.url.ComponentUrlParams;

final class ComponentUrlFunction
    extends AbstractUrlFunction
{
    public ComponentUrlFunction()
    {
        super( "componentUrl" );
    }

    @Override
    protected String execute( final Multimap<String, String> map )
    {
        final ComponentUrlParams params = new ComponentUrlParams().context( getContext() ).setAsMap( map );
        return this.urlService.componentUrl( params );
    }
}
