package com.enonic.wem.admin.rest.rpc.schema.mixin;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.admin.json.JsonResult;

final class CreateOrUpdateMixinJsonResult
    extends JsonResult
{
    private final boolean created;

    private CreateOrUpdateMixinJsonResult( final boolean created )
    {
        this.created = created;
    }

    public static CreateOrUpdateMixinJsonResult created()
    {
        return new CreateOrUpdateMixinJsonResult( true );
    }

    public static CreateOrUpdateMixinJsonResult updated()
    {
        return new CreateOrUpdateMixinJsonResult( false );
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "created", created );
        json.put( "updated", !created );
    }
}
