package com.enonic.wem.core.content.type.configitem.fieldtype;


import org.apache.commons.lang.StringUtils;

import com.enonic.wem.core.content.data.Value;
import com.enonic.wem.core.content.type.valuetype.ValueTypes;

public class HtmlArea
    extends BaseFieldType
{
    HtmlArea()
    {
        super( "htmlArea", ValueTypes.HTML_PART );
    }

    @Override
    public boolean validValue( final Value fieldValue )
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

    @Override
    public boolean breaksRequiredContract( final Value value )
    {
        String stringValue = (String) value.getValue();
        return StringUtils.isBlank( stringValue );
    }
}
