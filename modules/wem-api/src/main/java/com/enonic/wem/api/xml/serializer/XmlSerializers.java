package com.enonic.wem.api.xml.serializer;

import com.enonic.wem.api.xml.model.XmlContentType;
import com.enonic.wem.api.xml.model.XmlForm;
import com.enonic.wem.api.xml.model.XmlLayoutDescriptor;
import com.enonic.wem.api.xml.model.XmlMetadataSchema;
import com.enonic.wem.api.xml.model.XmlMixin;
import com.enonic.wem.api.xml.model.XmlModule;
import com.enonic.wem.api.xml.model.XmlPageDescriptor;
import com.enonic.wem.api.xml.model.XmlPartDescriptor;
import com.enonic.wem.api.xml.model.XmlRelationshipType;

public final class XmlSerializers
{
    private final static XmlFormSerializer FORM = new XmlFormSerializer();

    private final static XmlModuleSerializer MODULE = new XmlModuleSerializer();

    private final static XmlContentTypeSerializer CONTENT_TYPE = new XmlContentTypeSerializer();

    private final static XmlMixinSerializer MIXIN = new XmlMixinSerializer();

    private final static XmlRelationshipTypeSerializer RELATIONSHIP_TYPE = new XmlRelationshipTypeSerializer();

    private final static XmlMetadataSchemaSerializer METADATA_SCHEMA = new XmlMetadataSchemaSerializer();

    private final static XmlLayoutDescriptorSerializer LAYOUT_DESCRIPTOR = new XmlLayoutDescriptorSerializer();

    private final static XmlPartDescriptorSerializer PART_DESCRIPTOR = new XmlPartDescriptorSerializer();

    private final static XmlPageDescriptorSerializer PAGE_DESCRIPTOR = new XmlPageDescriptorSerializer();

    public static XmlSerializer<XmlForm> form()
    {
        return FORM;
    }

    public static XmlSerializer<XmlModule> module()
    {
        return MODULE;
    }

    public static XmlSerializer<XmlMixin> mixin()
    {
        return MIXIN;
    }

    public static XmlSerializer<XmlContentType> contentType()
    {
        return CONTENT_TYPE;
    }

    public static XmlSerializer<XmlRelationshipType> relationshipType()
    {
        return RELATIONSHIP_TYPE;
    }

    public static XmlSerializer<XmlMetadataSchema> metadataSchema()
    {
        return METADATA_SCHEMA;
    }

    public static XmlSerializer<XmlPageDescriptor> pageDescriptor()
    {
        return PAGE_DESCRIPTOR;
    }

    public static XmlSerializer<XmlPartDescriptor> partDescriptor()
    {
        return PART_DESCRIPTOR;
    }

    public static XmlSerializer<XmlLayoutDescriptor> layoutDescriptor()
    {
        return LAYOUT_DESCRIPTOR;
    }

}
