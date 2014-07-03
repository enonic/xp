package com.enonic.wem.api.xml.mapper;

public interface XmlMapper<O, X>
{
    public O fromXml( X xml );

    public X toXml( O object );
}
