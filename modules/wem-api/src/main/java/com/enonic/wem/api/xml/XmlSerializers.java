package com.enonic.wem.api.xml;

import com.enonic.wem.api.content.page.PageTemplateXml;
import com.enonic.wem.api.content.page.image.ImageComponentXml;
import com.enonic.wem.api.content.page.layout.LayoutComponentXml;
import com.enonic.wem.api.content.page.part.PartComponentXml;
import com.enonic.wem.api.content.site.SiteTemplateXml;
import com.enonic.wem.api.form.FormXml;
import com.enonic.wem.api.form.MixinReferenceXml;
import com.enonic.wem.api.schema.content.ContentTypeXml;
import com.enonic.wem.api.schema.relationship.RelationshipTypeXml;

public final class XmlSerializers
{
    public static <T extends XmlObject> XmlSerializer<T> create( final Class<T> type )
    {
        return new XmlSerializerImpl<>( type );
    }

    public static XmlSerializer<RelationshipTypeXml> relationshipType()
    {
        return create( RelationshipTypeXml.class );
    }

    public static XmlSerializer<SiteTemplateXml> siteTemplate()
    {
        return create( SiteTemplateXml.class );
    }

    public static XmlSerializer<FormXml> form()
    {
        return create( FormXml.class );
    }

    public static XmlSerializer<MixinReferenceXml> mixinReference()
    {
        return create( MixinReferenceXml.class );
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

    public static XmlSerializer<ContentTypeXml> contentType()
    {
        return create( ContentTypeXml.class );
    }
}
