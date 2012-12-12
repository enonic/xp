package com.enonic.wem.web.rest.rpc.content.type;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.type.form.SubType;
import com.enonic.wem.core.content.type.SubTypeJsonSerializer;
import com.enonic.wem.web.json.JsonResult;

final class GetSubTypeRpcJsonResult
    extends JsonResult
{
    private final static SubTypeJsonSerializer subTypeJsonSerializer = new SubTypeJsonSerializer();

    private final SubType subType;

    public GetSubTypeRpcJsonResult( final SubType subType )
    {
        this.subType = subType;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "subType", subTypeJsonSerializer.toJson( subType ) );
    }
}
