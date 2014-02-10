package com.enonic.wem.api.form;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.w3c.dom.Element;

import com.enonic.wem.api.form.inputtype.ConfigXml;
import com.enonic.wem.xml.XmlSerializers;

public class ConfigXmlAdapter
    extends XmlAdapter<Object, ConfigXml>
{
    @Override
    public ConfigXml unmarshal( final Object object )
        throws Exception
    {
        final Element element = Element.class.cast( object );

        return new ConfigXml(element) ;
    }

    @Override
    public Object marshal( final ConfigXml configXml )
        throws Exception
    {
        if ( configXml != null)
        {
            return XmlSerializers.create( configXml.getClass() ).serializeToNode( configXml );
        }

        return null;
    }
}
