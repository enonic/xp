package com.enonic.wem.admin.json.schema.relationship;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import com.enonic.wem.admin.json.ItemJson;
import com.enonic.wem.admin.rest.resource.schema.SchemaImageUriResolver;
import com.enonic.wem.api.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipType;

public class RelationshipTypeResultJson
    extends ItemJson
{
    private final RelationshipType model;

    private final String iconUrl;

    private final List<String> allowedFromTypes;

    private final List<String> allowedToTypes;

    public RelationshipTypeResultJson( final RelationshipType model )
    {
        this.model = model;
        this.iconUrl = SchemaImageUriResolver.resolve( model.getSchemaKey() );

        this.allowedFromTypes = new ArrayList<>( model.getAllowedFromTypes().getSize() );
        for ( QualifiedContentTypeName allowedFromType : model.getAllowedFromTypes() )
        {
            this.allowedFromTypes.add( allowedFromType.toString() );
        }

        this.allowedToTypes = new ArrayList<>( model.getAllowedToTypes().getSize() );
        for ( QualifiedContentTypeName allowedToType : model.getAllowedToTypes() )
        {
            this.allowedToTypes.add( allowedToType.toString() );
        }
    }

    public String getIconUrl()
    {
        return iconUrl;
    }

    public String getName()
    {
        return model.getName();
    }

    public String getModule()
    {
        return model.getModuleName().toString();
    }

    public String getDisplayName()
    {
        return model.getDisplayName();
    }

    public String getFromSemantic()
    {
        return model.getFromSemantic();
    }

    public String getToSemantic()
    {
        return model.getToSemantic();
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
        return model.getCreatedTime();
    }

    public DateTime getModifiedTime()
    {
        return model.getModifiedTime();
    }

    @Override
    public boolean getEditable()
    {
        return false;
    }

    @Override
    public boolean getDeletable()
    {
        return false;
    }
}
