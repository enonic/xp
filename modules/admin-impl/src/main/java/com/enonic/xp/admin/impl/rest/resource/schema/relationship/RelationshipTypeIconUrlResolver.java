package com.enonic.xp.admin.impl.rest.resource.schema.relationship;

import com.enonic.xp.admin.impl.rest.resource.schema.IconUrlResolver;
import com.enonic.wem.api.Icon;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;

public final class RelationshipTypeIconUrlResolver
    extends IconUrlResolver
{

    public static final String REST_SCHEMA_ICON_URL = "/admin/rest/schema/relationship/icon/";

    private final RelationshipTypeIconResolver relationshipTypeIconResolver;

    public RelationshipTypeIconUrlResolver( final RelationshipTypeIconResolver relationshipTypeIconResolver )
    {
        this.relationshipTypeIconResolver = relationshipTypeIconResolver;
    }

    public String resolve( final RelationshipType relationshipType )
    {
        final String baseUrl = REST_SCHEMA_ICON_URL + relationshipType.getName().toString();
        final Icon icon = relationshipType.getIcon();
        return generateIconUrl( baseUrl, icon );
    }

    public String resolve( final RelationshipTypeName relationshipTypeName )
    {
        final String baseUrl = REST_SCHEMA_ICON_URL + relationshipTypeName.toString();
        final Icon icon = relationshipTypeIconResolver.resolveIcon( relationshipTypeName );
        return generateIconUrl( baseUrl, icon );
    }
}
