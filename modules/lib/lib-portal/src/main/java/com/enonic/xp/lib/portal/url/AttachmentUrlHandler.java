package com.enonic.xp.lib.portal.url;

import com.enonic.xp.portal.url.AttachmentUrlParams;
import com.google.common.collect.Multimap;

import java.util.Arrays;
import java.util.List;

public final class AttachmentUrlHandler
    extends AbstractUrlHandler
{
    @Override
    protected String buildUrl( final Multimap<String, String> map )
    {
        final AttachmentUrlParams params = new AttachmentUrlParams().portalRequest( this.request ).setAsMap( map );
        return this.urlService.attachmentUrl( params );
    }

    @Override
    protected List<String> getValidUrlPropertyKeys() {
        return Arrays.asList("id", "path", "name", "type", "download", "label", "params");
    }
}
