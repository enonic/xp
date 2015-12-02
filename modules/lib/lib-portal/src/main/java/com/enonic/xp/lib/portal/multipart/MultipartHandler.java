package com.enonic.xp.lib.portal.multipart;


import com.google.common.collect.ImmutableList;
import com.google.common.io.ByteSource;
import com.google.common.net.MediaType;

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
    private final static ImmutableList<MediaType> TEXT_CONTENT_TYPES =
        ImmutableList.of( MediaType.ANY_TEXT_TYPE, MediaType.create( "application", "json" ) );

    private MultipartForm form;

    public MultipartFormMapper getForm()
    {
        return new MultipartFormMapper( this.form );
    }

    public MultipartItemMapper getItem( final String name )
    {
        if ( this.form == null )
        {
            return null;
        }

        final MultipartItem item = this.form.get( name );
        return item != null ? new MultipartItemMapper( item ) : null;
    }

    public ByteSource getBytes( final String name )
    {
        if ( this.form == null )
        {
            return null;
        }

        final MultipartItem item = this.form.get( name );
        return item != null ? item.getBytes() : null;
    }

    public String getText( final String name )
    {
        if ( this.form == null )
        {
            return null;
        }

        final MultipartItem item = this.form.get( name );
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
        final PortalRequest request = PortalRequestAccessor.get();
        final MultipartService service = context.getService( MultipartService.class ).get();
        this.form = service.parse( request.getRawRequest() );
    }
}
