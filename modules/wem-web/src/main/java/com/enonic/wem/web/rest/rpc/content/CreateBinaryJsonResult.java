package com.enonic.wem.web.rest.rpc.content;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.binary.BinaryId;
import com.enonic.wem.web.json.JsonResult;

final class CreateBinaryJsonResult
    extends JsonResult
{
    private final BinaryId binaryId;

    public CreateBinaryJsonResult( final BinaryId binaryId )
    {
        this.binaryId = binaryId;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "binaryId", binaryId.toString() );
    }

}
