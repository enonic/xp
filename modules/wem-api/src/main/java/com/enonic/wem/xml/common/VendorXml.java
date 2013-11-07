package com.enonic.wem.xml.common;

import javax.xml.bind.annotation.XmlElement;

public final class VendorXml
{
    @XmlElement(name = "name")
    private String name;

    @XmlElement(name = "url")
    private String url;
}
