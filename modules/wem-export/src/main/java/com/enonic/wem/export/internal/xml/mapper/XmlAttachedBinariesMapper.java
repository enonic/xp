package com.enonic.wem.export.internal.xml.mapper;

import com.enonic.wem.api.node.AttachedBinaries;
import com.enonic.wem.api.node.AttachedBinary;
import com.enonic.wem.export.internal.xml.XmlAttachedBinaries;

public class XmlAttachedBinariesMapper
{
    static XmlAttachedBinaries toXml( final AttachedBinaries attachedBinaries )
    {
        XmlAttachedBinaries xmlAttachedBinaries = new XmlAttachedBinaries();

        for ( final AttachedBinary attachedBinary : attachedBinaries )
        {
            xmlAttachedBinaries.getAttachedBinary().add( XmlAttachedBinaryMapper.toXml( attachedBinary ) );
        }

        return xmlAttachedBinaries;
    }
}
