package com.enonic.xp.lib.portal.current;

import com.enonic.xp.content.Content;
import com.enonic.xp.lib.content.mapper.ContentMapper;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.bean.BeanContext;
import com.enonic.xp.portal.bean.ScriptBean;

public final class GetCurrentContentHandler
    implements ScriptBean
{
    private PortalRequest request;

    public ContentMapper execute()
    {
        final Content content = this.request.getContent();
        return content != null ? convert( content ) : null;
    }

    private ContentMapper convert( final Content content )
    {
        return content == null ? null : new ContentMapper( content );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.request = context.getRequest().get();
    }
}
