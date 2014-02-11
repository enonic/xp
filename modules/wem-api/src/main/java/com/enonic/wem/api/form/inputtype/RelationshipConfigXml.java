package com.enonic.wem.api.form.inputtype;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.enonic.wem.api.schema.relationship.RelationshipTypeName;

@XmlRootElement(name = "config")
public class RelationshipConfigXml
    extends ConfigXml<RelationshipConfig, RelationshipConfig.Builder>
{
    @XmlElement(name = "relationship-type", required = true)
    private String relationshipType;

    @Override
    public void from( final RelationshipConfig input )
    {
        this.relationshipType = input.getRelationshipType().toString();
    }

    @Override
    public void to( final RelationshipConfig.Builder output )
    {
        output.relationshipType( RelationshipTypeName.from( this.relationshipType ) );
    }
}
