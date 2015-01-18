package com.enonic.wem.api.xml.serializer;

import com.enonic.wem.api.xml.model.XmlContentType;
import com.enonic.wem.api.xml.model.XmlLayoutDescriptor;
import com.enonic.wem.api.xml.model.XmlMixin;
import com.enonic.wem.api.xml.model.XmlModule;
import com.enonic.wem.api.xml.model.XmlPageDescriptor;
import com.enonic.wem.api.xml.model.XmlPartDescriptor;
import com.enonic.wem.api.xml.model.XmlRelationshipType;

public final class XmlSerializers
{
    private final static XmlSerializer<XmlModule> MODULE = XmlSerializer.create( XmlModule.class );

    private final static XmlSerializer<XmlContentType> CONTENT_TYPE = XmlSerializer.create( XmlContentType.class );

    private final static XmlSerializer<XmlMixin> MIXIN = XmlSerializer.create( XmlMixin.class );

    private final static XmlSerializer<XmlRelationshipType> RELATIONSHIP_TYPE = XmlSerializer.create( XmlRelationshipType.class );

    private final static XmlSerializer<XmlLayoutDescriptor> LAYOUT_DESCRIPTOR = XmlSerializer.create( XmlLayoutDescriptor.class );

    private final static XmlSerializer<XmlPartDescriptor> PART_DESCRIPTOR = XmlSerializer.create( XmlPartDescriptor.class );

    private final static XmlSerializer<XmlPageDescriptor> PAGE_DESCRIPTOR = XmlSerializer.create( XmlPageDescriptor.class );

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
