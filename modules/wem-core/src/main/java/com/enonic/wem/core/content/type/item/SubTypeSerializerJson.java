package com.enonic.wem.core.content.type.item;


import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;

public class SubTypeSerializerJson
    extends ConfigItemSerializerJson
{
    public static final SubTypeSerializerJson DEFAULT = new SubTypeSerializerJson();

    public void generate( ConfigItem configItem, JsonGenerator g )
        throws IOException
    {
        SubType subType = (SubType) configItem;

        g.writeStartObject();
        g.writeStringField( "type", subType.getItemType().toString() );
        g.writeStringField( "path", subType.getPath().toString() );
        g.writeStringField( "name", subType.getName() );
        g.writeStringField( "label", subType.getLabel() );
        g.writeBooleanField( "required", subType.isRequired() );
        g.writeBooleanField( "immutable", subType.isImmutable() );
        MultipleSerializerJson.generate( subType.getMultiple(), g );
        g.writeStringField( "customText", subType.getCustomText() );
        g.writeStringField( "helpText", subType.getHelpText() );
        ConfigItemsSerializerJson.generate( subType.getConfigItems(), g );

        g.writeEndObject();
    }
}
