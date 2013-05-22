package com.enonic.wem.web.rest.rpc.relationship;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.web.json.JsonResult;

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
