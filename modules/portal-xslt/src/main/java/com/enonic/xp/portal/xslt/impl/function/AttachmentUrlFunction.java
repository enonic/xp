package com.enonic.xp.portal.xslt.impl.function;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.url.PortalUrlBuildersHelper;

final class AttachmentUrlFunction
    extends AbstractUrlFunction
{
    public AttachmentUrlFunction()
    {
        super( "attachmentUrl" );
    }

    @Override
    protected String execute( final Multimap<String, String> params )
    {
        return PortalUrlBuildersHelper.apply( createUrlBuilders().attachmentUrl(), params ).toString();
    }
}
