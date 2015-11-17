package com.enonic.xp.lib.portal.multipart;

import com.google.common.io.ByteSource;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.web.multipart.MultipartForm;
import com.enonic.xp.web.multipart.MultipartItem;
import com.enonic.xp.web.multipart.MultipartService;

public final class MultipartHandler
    implements ScriptBean
{
    private MultipartForm form;

    public MultipartFormMapper getForm()
    {
        return new MultipartFormMapper( this.form );
    }

    public ByteSource getData( final String name )
    {
        if ( this.form == null )
        {
            return null;
        }

        final MultipartItem item = this.form.get( name );
        return item != null ? item.getBytes() : null;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        final PortalRequest request = PortalRequestAccessor.get();
        final MultipartService service = context.getService( MultipartService.class ).get();
        this.form = service.parse( request.getRawRequest() );
    }
}
