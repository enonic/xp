package com.enonic.xp.lib.portal.multipart;


import java.util.List;
import java.util.Objects;

import com.google.common.io.ByteSource;
import com.google.common.net.MediaType;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.web.multipart.MultipartForm;
import com.enonic.xp.web.multipart.MultipartItem;
import com.enonic.xp.web.multipart.MultipartService;

public final class MultipartHandler
    implements ScriptBean
{
    private static final List<MediaType> TEXT_CONTENT_TYPES =
        List.of( MediaType.ANY_TEXT_TYPE, MediaType.JSON_UTF_8.withoutParameters() );

    private MultipartForm form;

    public MultipartFormMapper getForm()
    {
        return new MultipartFormMapper( this.form );
    }

    public MultipartItemMapper getItem( final String name, final int index )
    {
        final MultipartItem item = this.form.get( name, index );
        return item != null ? new MultipartItemMapper( item ) : null;
    }

    public ByteSource getBytes( final String name, final int index )
    {
        final MultipartItem item = this.form.get( name, index );
        return item != null ? item.getBytes() : null;
    }

    public String getText( final String name, final int index )
    {
        final MultipartItem item = this.form.get( name, index );
        if ( item == null )
        {
            return null;
        }
        final MediaType contentType = item.getContentType();
        if ( contentType == null || TEXT_CONTENT_TYPES.stream().anyMatch( contentType::is ) )
        {
            return item.getAsString();
        }
        return null;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        final PortalRequest request = Objects.requireNonNull( context.getBinding( PortalRequest.class ).get(), "no request bound" );
        final MultipartService service = context.getService( MultipartService.class ).get();
        this.form = service.parse( request.getRawRequest() );
    }
}
