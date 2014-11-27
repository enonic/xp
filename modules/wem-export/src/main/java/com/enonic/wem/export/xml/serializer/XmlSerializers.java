package com.enonic.wem.export.xml.serializer;

import com.enonic.wem.export.xml.model.XmlNode;

public final class XmlSerializers
{
    private final static XmlSerializer<XmlNode> NODE = XmlSerializer.create( XmlNode.class );

    public static XmlSerializer<XmlNode> node()
    {
        return NODE;
    }
}
