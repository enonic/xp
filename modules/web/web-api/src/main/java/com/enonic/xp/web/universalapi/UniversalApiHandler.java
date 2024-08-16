package com.enonic.xp.web.universalapi;

import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

public interface UniversalApiHandler
{
    WebResponse handle( WebRequest request );
}
