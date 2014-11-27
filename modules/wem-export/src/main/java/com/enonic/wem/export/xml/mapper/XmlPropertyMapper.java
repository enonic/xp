package com.enonic.wem.export.xml.mapper;

import com.enonic.wem.api.data2.Property;
import com.enonic.wem.export.ExportNodeException;
import com.enonic.wem.api.xml.model.XmlProperty;

public class XmlPropertyMapper
{

    public static XmlProperty toXml( final Property property )
    {
        try
        {
            return doSerializeProperty( property );
        }
        catch ( Exception e )
        {
            throw new ExportNodeException( "Failed to serialize node to xml", e );
        }
    }

    private static XmlProperty doSerializeProperty( final Property property )
    {
        final XmlProperty xmlProperty = new XmlProperty();

        return null;
    }


}
