package com.enonic.wem.admin.rpc.relationship;

import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.admin.json.JsonResult;

final class UpdateRelationshipPropertiesJsonResult
    extends JsonResult
{
    private UpdateRelationshipPropertiesJsonResult( final Builder builder )
    {
        super( builder );
    }

    @Override
    protected void serialize( final ObjectNode json )
    {

    }

    public static Builder newBuilder()
    {
        return new Builder();
    }

    public static class Builder
        extends JsonResultBuilder
    {

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
