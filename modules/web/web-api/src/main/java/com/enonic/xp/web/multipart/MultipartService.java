package com.enonic.xp.web.multipart;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

public interface MultipartService
{
    MultipartForm parse( HttpServletRequest req );

    MultipartForm parse( InputStream in, String type );
}
