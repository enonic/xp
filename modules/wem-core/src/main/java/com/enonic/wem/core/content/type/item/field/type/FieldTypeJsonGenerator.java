package com.enonic.wem.core.content.type.item.field.type;


import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;

public abstract class FieldTypeJsonGenerator
{
    public abstract void generate( FieldType fieldType, JsonGenerator g )
        throws IOException;
}
