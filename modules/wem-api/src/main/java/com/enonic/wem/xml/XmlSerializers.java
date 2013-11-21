package com.enonic.wem.xml;

import com.enonic.wem.xml.template.ImageTemplateXml;
import com.enonic.wem.xml.template.LayoutTemplateXml;
import com.enonic.wem.xml.template.PageTemplateXml;
import com.enonic.wem.xml.template.PartTemplateXml;
import com.enonic.wem.xml.template.SiteTemplateXml;

public final class XmlSerializers
{
    public static <T extends XmlObject> XmlSerializer<T> create( final Class<T> type )
    {
        return new XmlSerializerImpl<>( type );
    }

    public static XmlSerializer<SiteTemplateXml> siteTemplate()
    {
        return create( SiteTemplateXml.class );
    }

    public static XmlSerializer<PageTemplateXml> pageTemplate()
    {
        return create( PageTemplateXml.class );
    }

    public static XmlSerializer<PartTemplateXml> partTemplate()
    {
        return create( PartTemplateXml.class );
    }

    public static XmlSerializer<LayoutTemplateXml> layoutTemplate()
    {
        return create( LayoutTemplateXml.class );
    }

    public static XmlSerializer<ImageTemplateXml> imageTemplate()
    {
        return create( ImageTemplateXml.class );
    }

}
