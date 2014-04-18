package com.enonic.wem.xml.module;

import javax.xml.bind.JAXBContext;
import javax.xml.validation.Schema;

import com.enonic.wem.xml.XmlBeanHelper;

public final class XmlModuleHelper
    implements XmlBeanHelper<XmlModule>
{
    private JAXBContext context;

    public XmlModuleHelper()
        throws Exception
    {
        this.context = JAXBContext.newInstance( ObjectFactory.class );
    }

    @Override
    public Schema getSchema()
    {
        return null;
    }

    @Override
    public XmlBeanHelper<XmlModule> validation( final boolean flag )
    {
        return null;
    }

    @Override
    public XmlModule parse( final String value )
    {
        return null;
    }

    @Override
    public String serialize( final XmlModule model )
    {
        return null;
    }
}
