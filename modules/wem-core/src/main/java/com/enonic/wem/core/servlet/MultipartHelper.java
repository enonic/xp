package com.enonic.wem.core.servlet;

import javax.mail.internet.ContentDisposition;
import javax.servlet.http.Part;

import com.google.common.base.Strings;

public final class MultipartHelper
{
    public static String getFileName( final Part part )
        throws Exception
    {
        final String value = part.getHeader( "content-disposition" );
        if ( Strings.isNullOrEmpty( value ) )
        {
            return null;
        }

        final ContentDisposition disposition = new ContentDisposition( value );
        return disposition.getParameter( "filename" );
    }
}
