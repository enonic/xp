package com.enonic.wem.api.content.type.formitem.comptype;


import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.datatype.DataTypes;
import com.enonic.wem.api.content.type.formitem.BreaksRequiredContractException;

public class HtmlArea
    extends BaseComponentType
{
    public HtmlArea()
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
