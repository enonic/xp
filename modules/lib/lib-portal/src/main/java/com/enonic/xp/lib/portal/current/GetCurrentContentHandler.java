package com.enonic.xp.lib.portal.current;

import com.enonic.xp.content.Content;
import com.enonic.xp.lib.content.mapper.ContentMapper;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

import static java.util.Objects.requireNonNull;

public final class GetCurrentContentHandler
    implements ScriptBean
{
    private PortalRequest request;

    public ContentMapper execute()
    {
        final Content content = this.request.getContent();
        return content != null ? new ContentMapper( content ) : null;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.request = requireNonNull( context.getBinding( PortalRequest.class ).get(), "no request bound" );
    }
}
