package com.enonic.xp.portal.universalapi;

import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

public interface UniversalApiHandler
{
    WebResponse handle( WebRequest request );
}
