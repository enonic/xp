package com.enonic.wem.xml;

import com.enonic.wem.xml.template.PageXmlSerializer;

public final class XmlSerializers
{
    public static PageXmlSerializer pageTemplate()
    {
        return new PageXmlSerializer();
    }
}
