package com.enonic.wem.export.internal.xml.mapper;

import com.enonic.wem.api.node.AttachedBinary;
import com.enonic.wem.export.internal.xml.XmlAttachedBinaries;

public class XmlAttachedBinaryMapper
{
    static XmlAttachedBinaries.AttachedBinary toXml( final AttachedBinary attachedBinary )
    {
        final XmlAttachedBinaries.AttachedBinary xmlAttachedBinary = new XmlAttachedBinaries.AttachedBinary();

        xmlAttachedBinary.setBinaryReference( attachedBinary.getBinaryReference().toString() );
        xmlAttachedBinary.setBlobKey( attachedBinary.getBlobKey().toString() );

        return xmlAttachedBinary;
    }
}
