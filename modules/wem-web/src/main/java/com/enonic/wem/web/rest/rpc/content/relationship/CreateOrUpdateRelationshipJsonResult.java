package com.enonic.wem.web.rest.rpc.content.relationship;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.relationship.RelationshipId;
import com.enonic.wem.web.json.JsonResult;

final class CreateOrUpdateRelationshipJsonResult
    extends JsonResult
{
    private final boolean created;

    private final RelationshipId relationshipId;

    private CreateOrUpdateRelationshipJsonResult( final Builder builder )
    {
        super( builder );
        this.created = builder.created;
        this.relationshipId = builder.relationshipId;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "created", created && !hasError() );
        json.put( "updated", !created && !hasError() );
        if ( relationshipId != null )
        {
            json.put( "relationshipId", relationshipId.toString() );
        }
    }

    public static CreateOrUpdateRelationshipJsonResult created( final RelationshipId relationshipId )
    {
        return new CreateOrUpdateRelationshipJsonResult( newBuilder().created().relationship( relationshipId ) );
    }

    public static Builder newBuilder()
    {
        return new Builder();
    }

    public static class Builder
        extends JsonResultBuilder
    {
        private boolean created;

        private RelationshipId relationshipId;

        public Builder created()
        {
            created = true;
            return this;
        }

        public Builder updated()
        {
            created = false;
            return this;
        }

        public Builder relationship( final RelationshipId relationshipId )
        {
            this.relationshipId = relationshipId;
            return this;
        }

        public Builder failure( String reason )
        {
            this.error( reason );
            return this;
        }

        public CreateOrUpdateRelationshipJsonResult build()
        {
            return new CreateOrUpdateRelationshipJsonResult( this );
        }
    }
}
