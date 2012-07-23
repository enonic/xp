package com.enonic.wem.core.content.config.field;


import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;

public class SubTypeJsonGenerator
    extends ConfigItemJsonGenerator
{
    public static final SubTypeJsonGenerator DEFAULT = new SubTypeJsonGenerator();

    public void generate( ConfigItem configItem, JsonGenerator g )
        throws IOException
    {
        SubType subType = (SubType) configItem;

        g.writeStartObject();
        g.writeStringField( "type", subType.getConfigType().toString() );
        g.writeStringField( "path", subType.getPath().toString() );
        g.writeStringField( "name", subType.getName() );
        g.writeStringField( "label", subType.getLabel() );
        g.writeBooleanField( "required", subType.isRequired() );
        g.writeBooleanField( "immutable", subType.isImmutable() );
        MultipleJsonGenerator.generate( subType.getMultiple(), g );
        g.writeStringField( "customText", subType.getCustomText() );
        g.writeStringField( "helpText", subType.getHelpText() );
        ConfigItemsJsonGenerator.generate( subType.getConfigItems(), g );

        g.writeEndObject();
    }
}
