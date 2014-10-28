package com.enonic.wem.api.xml.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.collect.Lists;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "metadata-schemas")
public final class XmlMetadataSchemas
{
    @XmlElement(name = "metadata-schema")
    private List<XmlMetadataSchema> metadataSchema = Lists.newArrayList();

    public List<XmlMetadataSchema> getMetadataSchema()
    {
        return this.metadataSchema;
    }
}
