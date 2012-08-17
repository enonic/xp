package com.enonic.wem.core.content.type.configitem.fieldtype;

import com.enonic.wem.core.content.data.Value;

public class HtmlAreaConfig
    implements FieldTypeConfig
{
    @Override
    public boolean isValid( final Value value )
    {
        return true;
    }
}
