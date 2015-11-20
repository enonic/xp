package com.enonic.xp.web.multipart;

import javax.servlet.http.HttpServletRequest;

public interface MultipartService
{
    MultipartForm parse( HttpServletRequest req );
}
