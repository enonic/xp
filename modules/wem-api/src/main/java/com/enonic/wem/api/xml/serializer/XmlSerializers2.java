package com.enonic.wem.api.xml.serializer;

import com.enonic.wem.api.xml.model.XmlForm;
import com.enonic.wem.api.xml.model.XmlModule;

public final class XmlSerializers2
{
    private final static XmlFormSerializer FORM = new XmlFormSerializer();

    private final static XmlModuleSerializer MODULE = new XmlModuleSerializer();

    public static XmlSerializer2<XmlForm> form()
    {
        return FORM;
    }

    public static XmlSerializer2<XmlModule> module()
    {
        return MODULE;
    }
}
