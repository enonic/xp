package com.enonic.xp.web.multipart;

import jakarta.servlet.http.HttpServletRequest;

public interface MultipartService
{
    MultipartForm parse( HttpServletRequest req );
}
