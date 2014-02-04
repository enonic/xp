package com.enonic.wem.xml;

import com.enonic.wem.api.content.page.PageDescriptorXml;
import com.enonic.wem.api.content.page.PageTemplateXml;
import com.enonic.wem.api.content.page.image.ImageDescriptorXml;
import com.enonic.wem.api.content.page.image.ImageTemplateXml;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorXml;
import com.enonic.wem.api.content.page.layout.LayoutTemplateXml;
import com.enonic.wem.api.content.page.part.PartDescriptorXml;
import com.enonic.wem.api.content.page.part.PartTemplateXml;
import com.enonic.wem.api.content.site.SiteTemplateXml;
import com.enonic.wem.api.schema.relationship.RelationshipTypeXml;
import com.enonic.wem.api.schema.mixin.MixinXml;
import com.enonic.wem.api.schema.content.ContentTypeXml;
import com.enonic.wem.api.form.FormXml;
import com.enonic.wem.api.form.MixinReferenceXml;

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

    public static XmlSerializer<PageDescriptorXml> pageDescriptor()
    {
        return create( PageDescriptorXml.class );
    }

    public static XmlSerializer<PartDescriptorXml> partDescriptor()
    {
        return create( PartDescriptorXml.class );
    }

    public static XmlSerializer<LayoutDescriptorXml> layoutDescriptor()
    {
        return create( LayoutDescriptorXml.class );
    }

    public static XmlSerializer<MixinXml> mixin()
    {
        return create( MixinXml.class );
    }

    public static XmlSerializer<ImageDescriptorXml> imageDescriptor()
    {
        return create( ImageDescriptorXml.class );
    }

    public static XmlSerializer<ContentTypeXml> contentType()
    {
        return create( ContentTypeXml.class );
    }
}
