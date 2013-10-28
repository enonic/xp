package com.enonic.wem.admin.json.schema.relationship;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import com.enonic.wem.admin.json.ItemJson;
import com.enonic.wem.admin.rest.resource.schema.SchemaImageUriResolver;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipType;

public class RelationshipTypeJson
    implements ItemJson
{
    private final RelationshipType relationshipType;

    private final List<String> allowedFromTypes;

    private final List<String> allowedToTypes;

    private final String iconUrl;

    private final boolean editable;

    private final boolean deletable;

    public RelationshipTypeJson( final RelationshipType type )
    {
        this.relationshipType = type;
        this.editable = true;
        this.deletable = true;

        this.allowedFromTypes = new ArrayList<>( relationshipType.getAllowedFromTypes().getSize() );
        for ( ContentTypeName allowedFromType : relationshipType.getAllowedFromTypes() )
        {
            this.allowedFromTypes.add( allowedFromType.toString() );
        }

        this.allowedToTypes = new ArrayList<>( relationshipType.getAllowedToTypes().getSize() );
        for ( ContentTypeName allowedToType : relationshipType.getAllowedToTypes() )
        {
            this.allowedToTypes.add( allowedToType.toString() );
        }

        this.iconUrl = SchemaImageUriResolver.resolve( relationshipType.getSchemaKey() );
    }

    public String getIconUrl()
    {
        return iconUrl;
    }

    public String getName()
    {
        return relationshipType.getName();
    }

    public String getDisplayName()
    {
        return relationshipType.getDisplayName();
    }

    public String getFromSemantic()
    {
        return relationshipType.getFromSemantic();
    }

    public String getToSemantic()
    {
        return relationshipType.getToSemantic();
    }

    public List<String> getAllowedFromTypes()
    {
        return this.allowedFromTypes;
    }

    public List<String> getAllowedToTypes()
    {
        return allowedToTypes;
    }

    public DateTime getCreatedTime()
    {
        return relationshipType.getCreatedTime();
    }

    public DateTime getModifiedTime()
    {
        return relationshipType.getModifiedTime();
    }

    @Override
    public boolean getEditable()
    {
        return editable;
    }

    @Override
    public boolean getDeletable()
    {
        return deletable;
    }
}
