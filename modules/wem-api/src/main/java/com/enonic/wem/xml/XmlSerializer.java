package com.enonic.wem.xml;

public interface XmlSerializer<I, O>
{
    public String toXml( I value );

    public void fromXml( O value, String text );
}
