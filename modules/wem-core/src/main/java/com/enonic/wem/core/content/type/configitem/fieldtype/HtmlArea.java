package com.enonic.wem.core.content.type.configitem.fieldtype;


import org.apache.commons.lang.StringUtils;

import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.type.datatype.DataTypes;

public class HtmlArea
    extends BaseFieldType
{
    HtmlArea()
    {
        super( "htmlArea", DataTypes.HTML_PART );
    }

    @Override
    public boolean validData( final Data data )
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
    public boolean breaksRequiredContract( final Data data )
    {
        String stringValue = (String) data.getValue();
        return StringUtils.isBlank( stringValue );
    }
}
