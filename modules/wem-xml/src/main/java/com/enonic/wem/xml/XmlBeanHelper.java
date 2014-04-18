package com.enonic.wem.xml;

import javax.xml.validation.Schema;

public interface XmlBeanHelper<X>
{
    public Schema getSchema();

    public XmlBeanHelper<X> validation( boolean flag );

    public X parse( String value );

    public String serialize( X model );
}
