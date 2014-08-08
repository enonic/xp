package com.enonic.wem.api.xml.serializer;

import com.enonic.wem.api.xml.model.ObjectFactory;
import com.enonic.wem.api.xml.model.XmlPageTemplate;

public class XmlPageTemplateSerializer
    extends XmlSerializer2<XmlPageTemplate>
{

    public XmlPageTemplateSerializer( )
    {
        super( XmlPageTemplate.class );
    }

    @Override
    protected Object wrapXml( final XmlPageTemplate xml )
    {
        return new ObjectFactory().createPageTemplate( xml );
    }
}
