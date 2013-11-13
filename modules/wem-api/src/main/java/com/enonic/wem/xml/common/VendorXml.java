package com.enonic.wem.xml.common;

import javax.xml.bind.annotation.XmlElement;

public final class VendorXml
{
    @XmlElement(name = "name")
    public String name;

    @XmlElement(name = "url")
    public String url;
}
