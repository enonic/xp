package com.enonic.wem.core.content.type.configitem.fieldtype;


import org.apache.commons.lang.StringUtils;

import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.datatype.DataTypes;
import com.enonic.wem.core.content.type.configitem.BreaksRequiredContractException;

public class HtmlArea
    extends BaseFieldType
{
    HtmlArea()
    {
        super( "htmlArea", DataTypes.HTML_PART );
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
    public void checkBreaksRequiredContract( final Data data )
        throws BreaksRequiredContractException
    {
        final String stringValue = (String) data.getValue();
        if ( StringUtils.isBlank( stringValue ) )
        {
            throw new BreaksRequiredContractException( data, this );
        }
    }
}
