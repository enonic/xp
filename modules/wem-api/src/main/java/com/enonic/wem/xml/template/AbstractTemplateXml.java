package com.enonic.wem.xml.template;

import javax.xml.bind.annotation.XmlElement;

import com.enonic.wem.xml.XmlObject;
import com.enonic.wem.xml.common.VendorXml;

public abstract class AbstractTemplateXml<I, O>
    implements XmlObject<I, O>
{
    @XmlElement(name = "display-name", required = false)
    protected String displayName;

    @XmlElement(name = "info", required = false)
    protected String info;

    @XmlElement(name = "url", required = false)
    protected String url;

    @XmlElement(name = "vendor")
    protected VendorXml vendor;
}
