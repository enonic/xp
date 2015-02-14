package com.enonic.xp.core.xml.serializer;

import com.enonic.xp.core.xml.model.XmlContentType;
import com.enonic.xp.core.xml.model.XmlLayoutDescriptor;
import com.enonic.xp.core.xml.model.XmlMixin;
import com.enonic.xp.core.xml.model.XmlModule;
import com.enonic.xp.core.xml.model.XmlPageDescriptor;
import com.enonic.xp.core.xml.model.XmlPartDescriptor;
import com.enonic.xp.core.xml.model.XmlRelationshipType;

public final class XmlSerializers
{
    private final static XmlSerializer<XmlModule> MODULE = new XmlModuleSerializer();

    private final static XmlSerializer<XmlContentType> CONTENT_TYPE = new XmlContentTypeSerializer();

    private final static XmlSerializer<XmlMixin> MIXIN = new XmlMixinSerializer();

    private final static XmlSerializer<XmlRelationshipType> RELATIONSHIP_TYPE = new XmlRelationshipTypeSerializer();

    private final static XmlSerializer<XmlLayoutDescriptor> LAYOUT_DESCRIPTOR = new XmlLayoutDescriptorSerializer();

    private final static XmlSerializer<XmlPartDescriptor> PART_DESCRIPTOR = new XmlPartDescriptorSerializer();

    private final static XmlSerializer<XmlPageDescriptor> PAGE_DESCRIPTOR = new XmlPageDescriptorSerializer();

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
