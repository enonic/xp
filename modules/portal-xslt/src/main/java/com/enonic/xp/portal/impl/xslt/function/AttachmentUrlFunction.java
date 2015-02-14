package com.enonic.xp.portal.impl.xslt.function;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.url.AttachmentUrlParams;

final class AttachmentUrlFunction
    extends AbstractUrlFunction
{
    public AttachmentUrlFunction()
    {
        super( "attachmentUrl" );
    }

    @Override
    protected String execute( final Multimap<String, String> map )
    {
        final AttachmentUrlParams params = new AttachmentUrlParams().context( getContext() ).setAsMap( map );
        return this.urlService.attachmentUrl( params );
    }
}
