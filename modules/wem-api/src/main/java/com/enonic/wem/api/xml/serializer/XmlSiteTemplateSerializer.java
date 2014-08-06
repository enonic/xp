package com.enonic.wem.api.xml.serializer;

import com.enonic.wem.api.xml.model.ObjectFactory;
import com.enonic.wem.api.xml.model.XmlSiteTemplate;

public class XmlSiteTemplateSerializer
    extends XmlSerializer2<XmlSiteTemplate>
{
    public XmlSiteTemplateSerializer( )
    {
        super( XmlSiteTemplate.class );
    }

    @Override
    protected Object wrapXml( final XmlSiteTemplate xml )
    {
        return new ObjectFactory().createSiteTemplate( xml );
    }
}
