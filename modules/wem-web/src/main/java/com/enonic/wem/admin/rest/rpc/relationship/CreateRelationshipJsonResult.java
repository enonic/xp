package com.enonic.wem.admin.rest.rpc.relationship;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.admin.json.JsonResult;
import com.enonic.wem.api.relationship.RelationshipKey;

final class CreateRelationshipJsonResult
    extends JsonResult
{
    private final RelationshipKey relationshipId;

    private CreateRelationshipJsonResult( final Builder builder )
    {
        super( builder );
        this.relationshipId = builder.relationshipKey;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "created", !hasError() );
        if ( relationshipId != null )
        {
            ObjectNode relationshipKeyObj = json.putObject( "relationshipKey" );
            relationshipKeyObj.put( "fromContent", relationshipId.getFromContent().toString() );
            relationshipKeyObj.put( "toContent", relationshipId.getToContent().toString() );
            relationshipKeyObj.put( "type", relationshipId.getType().toString() );
        }
    }

    public static CreateRelationshipJsonResult created( final RelationshipKey relationshipKey )
    {
        return new CreateRelationshipJsonResult( newBuilder().relationship( relationshipKey ) );
    }

    public static Builder newBuilder()
    {
        return new Builder();
    }

    public static class Builder
        extends JsonResultBuilder
    {
        private RelationshipKey relationshipKey;


        public Builder relationship( final RelationshipKey value )
        {
            this.relationshipKey = value;
            return this;
        }

        public Builder failure( String reason )
        {
            this.error( reason );
            return this;
        }

        public CreateRelationshipJsonResult build()
        {
            return new CreateRelationshipJsonResult( this );
        }
    }
}
