package com.enonic.wem.web.rest.rpc.content.relationship;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.relationship.RelationshipKey;
import com.enonic.wem.web.json.JsonResult;

final class UpdateRelationshipPropertiesJsonResult
    extends JsonResult
{
    private final RelationshipKey relationshipKey;

    private UpdateRelationshipPropertiesJsonResult( final Builder builder )
    {
        super( builder );
        this.relationshipKey = builder.relationshipKey;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "updated", !hasError() );
        if ( relationshipKey != null )
        {
            ObjectNode relationshipKeyObj = json.putObject( "relationshipKey" );
            relationshipKeyObj.put( "fromContent", relationshipKey.getFromContent().toString() );
            relationshipKeyObj.put( "toContent", relationshipKey.getToContent().toString() );
            relationshipKeyObj.put( "type", relationshipKey.getType().toString() );
        }
    }

    public static UpdateRelationshipPropertiesJsonResult updated( final RelationshipKey relationshipKey )
    {
        return new UpdateRelationshipPropertiesJsonResult( newBuilder().relationship( relationshipKey ) );
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

        public UpdateRelationshipPropertiesJsonResult build()
        {
            return new UpdateRelationshipPropertiesJsonResult( this );
        }
    }
}
