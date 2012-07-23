package com.enonic.wem.core.content.config.field.type;


import com.enonic.wem.core.content.FieldValue;
import com.enonic.wem.core.content.config.field.type.value.ValueTypes;

public class HtmlArea
    extends AbstractBaseFieldType
{
    HtmlArea()
    {
        super( "htmlArea", ValueTypes.HTML_PART );
    }

    @Override
    public boolean validValue( final FieldValue fieldValue )
    {
        return true;
    }

    @Override
    public FieldTypeJsonGenerator getJsonGenerator()
    {
        return null;
    }

    @Override
    public boolean requiresConfig()
    {
        return true;
    }

    @Override
    public Class requiredConfigClass()
    {
        return HtmlAreaConfig.class;
    }
}
