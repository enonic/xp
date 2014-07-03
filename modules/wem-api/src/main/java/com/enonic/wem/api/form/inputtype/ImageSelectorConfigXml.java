package com.enonic.wem.api.form.inputtype;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.enonic.wem.api.schema.relationship.RelationshipTypeName;

@Deprecated
@XmlRootElement(name = "config")
public class ImageSelectorConfigXml
    extends ConfigXml<ImageSelectorConfig, ImageSelectorConfig.Builder>
{
    @XmlElement(name = "relationship-type", required = true)
    private String relationshipType;

    @Override
    public void from( final ImageSelectorConfig input )
    {
        this.relationshipType = input.getRelationshipType().toString();
    }

    @Override
    public void to( final ImageSelectorConfig.Builder output )
    {
        output.relationshipType( RelationshipTypeName.from( this.relationshipType ) );
    }
}
