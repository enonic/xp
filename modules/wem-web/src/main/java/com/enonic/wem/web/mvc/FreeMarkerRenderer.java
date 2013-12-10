package com.enonic.wem.web.mvc;

import java.io.IOException;

public interface FreeMarkerRenderer
{
    public String render( FreeMarkerView view )
        throws IOException;
}
