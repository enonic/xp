package com.enonic.wem.admin.json.schema.relationship;

import com.enonic.wem.admin.rest.resource.schema.SchemaImageUriResolver;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.core.schema.relationship.RelationshipTypeXmlSerializer;

public class RelationshipTypeConfigRpcJson
    extends AbstractRelationshipTypeJson
{
    private final String contentTypeXml;

    private final String iconUrl;

    public RelationshipTypeConfigRpcJson( final RelationshipType model )
    {
        this.contentTypeXml = new RelationshipTypeXmlSerializer().toString( model );
        this.iconUrl = SchemaImageUriResolver.resolve( model.getSchemaKey() );
    }

    public String getRelationshipTypeXml()
    {
        return this.contentTypeXml;
    }

    public String getIconUrl()
    {
        return this.iconUrl;
    }
}
