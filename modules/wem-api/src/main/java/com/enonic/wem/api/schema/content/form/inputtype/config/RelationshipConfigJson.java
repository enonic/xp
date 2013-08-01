package com.enonic.wem.api.schema.content.form.inputtype.config;

import com.enonic.wem.api.schema.content.form.inputtype.RelationshipConfig;

public class RelationshipConfigJson
    extends AbstractInputTypeConfigJson
{
    private final RelationshipConfig config;

    public RelationshipConfigJson( final RelationshipConfig config )
    {
        this.config = config;
    }

    public String getRelationshipType()
    {
        return ( config.getRelationshipType() != null ) ? config.getRelationshipType().toString() : null;
    }
}
