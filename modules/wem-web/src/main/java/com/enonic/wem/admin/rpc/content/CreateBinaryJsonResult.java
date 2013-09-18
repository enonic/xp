package com.enonic.wem.admin.rpc.content;

import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.admin.json.JsonResult;
import com.enonic.wem.api.content.binary.BinaryId;

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
