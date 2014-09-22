package com.enonic.wem.api.xml.serializer;

import com.enonic.wem.api.xml.model.XmlContentType;
import com.enonic.wem.api.xml.model.XmlForm;
import com.enonic.wem.api.xml.model.XmlImageDescriptor;
import com.enonic.wem.api.xml.model.XmlLayoutDescriptor;
import com.enonic.wem.api.xml.model.XmlMetadataSchema;
import com.enonic.wem.api.xml.model.XmlMixin;
import com.enonic.wem.api.xml.model.XmlModule;
import com.enonic.wem.api.xml.model.XmlPageDescriptor;
import com.enonic.wem.api.xml.model.XmlPageTemplate;
import com.enonic.wem.api.xml.model.XmlPartDescriptor;
import com.enonic.wem.api.xml.model.XmlRelationshipType;
import com.enonic.wem.api.xml.model.XmlSiteTemplate;

public final class XmlSerializers2
{
    private final static XmlFormSerializer FORM = new XmlFormSerializer();

    private final static XmlModuleSerializer MODULE = new XmlModuleSerializer();

    private final static XmlSiteTemplateSerializer SITE_TEMPLATE = new XmlSiteTemplateSerializer();

    private final static XmlPageTemplateSerializer PAGE_TEMPLATE = new XmlPageTemplateSerializer();

    private final static XmlContentTypeSerializer CONTENT_TYPE = new XmlContentTypeSerializer();

    private final static XmlMixinSerializer MIXIN = new XmlMixinSerializer();

    private final static XmlRelationshipTypeSerializer RELATIONSHIP_TYPE = new XmlRelationshipTypeSerializer();

    private final static XmlMetadataSchemaSerializer METADATA_SCHEMA = new XmlMetadataSchemaSerializer();

    private final static XmlLayoutDescriptorSerializer LAYOUT_DESCRIPTOR = new XmlLayoutDescriptorSerializer();

    private final static XmlImageDescriptorSerializer IMAGE_DESCRIPTOR = new XmlImageDescriptorSerializer();

    private final static XmlPartDescriptorSerializer PART_DESCRIPTOR = new XmlPartDescriptorSerializer();

    private final static XmlPageDescriptorSerializer PAGE_DESCRIPTOR = new XmlPageDescriptorSerializer();

    public static XmlSerializer2<XmlForm> form()
    {
        return FORM;
    }

    public static XmlSerializer2<XmlModule> module()
    {
        return MODULE;
    }

    public static XmlSerializer2<XmlMixin> mixin()
    {
        return MIXIN;
    }

    public static XmlSerializer2<XmlContentType> contentType()
    {
        return CONTENT_TYPE;
    }

    public static XmlSerializer2<XmlRelationshipType> relationshipType()
    {
        return RELATIONSHIP_TYPE;
    }

    public static XmlSerializer2<XmlMetadataSchema> metadataSchema()
    {
        return METADATA_SCHEMA;
    }

    public static XmlSerializer2<XmlSiteTemplate> siteTemplate()
    {
        return SITE_TEMPLATE;
    }

    public static XmlSerializer2<XmlPageDescriptor> pageDescriptor()
    {
        return PAGE_DESCRIPTOR;
    }

    public static XmlSerializer2<XmlPartDescriptor> partDescriptor()
    {
        return PART_DESCRIPTOR;
    }

    public static XmlSerializer2<XmlLayoutDescriptor> layoutDescriptor()
    {
        return LAYOUT_DESCRIPTOR;
    }

    public static XmlSerializer2<XmlImageDescriptor> imageDescriptor()
    {
        return IMAGE_DESCRIPTOR;
    }

    public static XmlSerializer2<XmlPageTemplate> pageTemplate()
    {
        return PAGE_TEMPLATE;
    }
}
