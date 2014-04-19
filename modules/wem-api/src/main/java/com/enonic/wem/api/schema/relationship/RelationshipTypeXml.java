package com.enonic.wem.api.schema.relationship;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Iterables;

import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.xml.XmlObject;

@XmlRootElement(name = "relationship-type")
public class RelationshipTypeXml
    implements XmlObject<RelationshipType, RelationshipType.Builder>
{
    @XmlElement(name = "display-name", required = false)
    private String displayName;

    @XmlElement(name = "description", required = false)
    private String description;

    @XmlElement(name = "from-semantic", required = false)
    private String fromSemantic;

    @XmlElement(name = "to-semantic", required = false)
    private String toSemantic;

    @XmlElement(name = "content-type", required = false)
    @XmlElementWrapper(name = "allowed-from-types")
    private List<String> allowedFromTypes = new ArrayList<>();

    @XmlElement(name = "content-type", required = false)
    @XmlElementWrapper(name = "allowed-to-types")
    private List<String> allowedToTypes = new ArrayList<>();

    @Override
    public void from( final RelationshipType relationshipType )
    {
        this.displayName = relationshipType.getDisplayName();
        this.description = relationshipType.getDescription();
        this.fromSemantic = relationshipType.getFromSemantic();
        this.toSemantic = relationshipType.getToSemantic();

        for ( final ContentTypeName allowedFromType : relationshipType.getAllowedFromTypes() )
        {
            this.allowedFromTypes.add( allowedFromType.toString() );
        }

        for ( final ContentTypeName allowedToType : relationshipType.getAllowedToTypes() )
        {
            this.allowedToTypes.add( allowedToType.toString() );
        }
    }

    @Override
    public void to( final RelationshipType.Builder builder )
    {
        builder.
            displayName( this.displayName ).
            description( this.description ).
            fromSemantic( this.fromSemantic ).
            toSemantic( this.toSemantic ).
            addAllowedFromTypes( ContentTypeNames.from( Iterables.toArray( this.allowedFromTypes, String.class ) ) ).
            addAllowedToTypes( ContentTypeNames.from( Iterables.toArray( this.allowedToTypes, String.class ) ) );
    }
}
