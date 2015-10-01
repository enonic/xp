package com.enonic.xp.web.impl.multipart;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.osgi.service.component.annotations.Component;

import com.enonic.xp.util.Exceptions;
import com.enonic.xp.web.multipart.MultipartForm;
import com.enonic.xp.web.multipart.MultipartService;

@Component
public final class MultipartServiceImpl
    implements MultipartService
{
    private final FileUpload fileUpload;

    public MultipartServiceImpl()
    {
        this.fileUpload = new FileUpload();
        this.fileUpload.setFileItemFactory( new DiskFileItemFactory() );
    }

    @Override
    public MultipartForm parse( final HttpServletRequest req )
    {
        return parse( new ServletRequestContext( req ) );
    }

    @Override
    public MultipartForm parse( final InputStream in, final String type )
    {
        return parse( new RequestContextImpl( in, type ) );
    }

    private MultipartForm parse( final RequestContext context )
    {
        try
        {
            return doParse( context );
        }
        catch ( final Exception e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    private MultipartForm doParse( final RequestContext context )
        throws Exception
    {
        return new MultipartFormImpl( this.fileUpload.parseRequest( context ) );
    }
}
