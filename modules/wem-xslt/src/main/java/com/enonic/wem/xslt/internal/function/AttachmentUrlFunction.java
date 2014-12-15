package com.enonic.wem.xslt.internal.function;

import com.google.common.collect.Multimap;

import com.enonic.wem.portal.url.PortalUrlBuildersHelper;

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
