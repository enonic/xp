package com.enonic.wem.core.mustache;

import java.io.Reader;
import java.util.Map;

public interface MustacheService
{
    public String render( Reader reader, Map<String, Object> model );
}
