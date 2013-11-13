package com.enonic.wem.xml.template;

import javax.xml.bind.annotation.XmlElement;

import com.enonic.wem.xml.XmlObject;

public abstract class AbstractTemplateXml<I, O>
    implements XmlObject<I, O>
{
    @XmlElement(name = "display-name", required = false)
    protected String displayName;

}
