package com.enonic.wem.core.content.type.configitem.fieldtype;

import com.enonic.wem.core.content.data.Data;

public class HtmlAreaConfig
    implements FieldTypeConfig
{
    @Override
    public boolean isValid( final Data data )
    {
        return true;
    }
}
