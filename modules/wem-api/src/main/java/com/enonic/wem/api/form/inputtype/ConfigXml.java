package com.enonic.wem.api.form.inputtype;

import org.w3c.dom.Element;

import com.enonic.wem.api.xml.XmlObject;

@Deprecated
public class ConfigXml<I extends InputTypeConfig, O>
    implements XmlObject<I, O>
{
    private Element element;

    public ConfigXml()
    {
    }

    public ConfigXml( final Element element )
    {
        this.element = element;
    }

    @Override
    public void from( final I input )
    {

    }

    @Override
    public void to( final O output )
    {
    }

    public Element getElement()
    {
        return element;
    }
}
