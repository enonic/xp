package com.enonic.wem.api.xml;

import com.enonic.wem.api.content.page.PageTemplateXml;
import com.enonic.wem.api.content.page.image.ImageComponentXml;
import com.enonic.wem.api.content.page.layout.LayoutComponentXml;
import com.enonic.wem.api.content.page.part.PartComponentXml;
import com.enonic.wem.api.content.site.SiteTemplateXml;

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

    public static XmlSerializer<PartComponentXml> partComponent()
    {
        return create( PartComponentXml.class );
    }

    public static XmlSerializer<LayoutComponentXml> layoutComponent()
    {
        return create( LayoutComponentXml.class );
    }

    public static XmlSerializer<ImageComponentXml> imageComponent()
    {
        return create( ImageComponentXml.class );
    }
}
