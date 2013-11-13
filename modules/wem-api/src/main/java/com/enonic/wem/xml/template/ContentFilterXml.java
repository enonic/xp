package com.enonic.wem.xml.template;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public final class ContentFilterXml
{
    @XmlElement(name = "deny")
    private List<String> deny;

    @XmlElement(name = "allow")
    private List<String> allow;
}
