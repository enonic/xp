package com.enonic.wem.core.content.config.field.type;


import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;

public interface FieldTypeConfigJsonParser
{
    public FieldTypeConfig parse( final JsonParser jp )
        throws IOException;

    public FieldTypeConfig parse( final JsonNode jp );
}
