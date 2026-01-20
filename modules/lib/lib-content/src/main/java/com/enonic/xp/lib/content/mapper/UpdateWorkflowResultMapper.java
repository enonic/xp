package com.enonic.xp.lib.content.mapper;

import com.enonic.xp.content.UpdateWorkflowResult;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public final class UpdateWorkflowResultMapper
    implements MapSerializable
{
    private final UpdateWorkflowResult result;

    public UpdateWorkflowResultMapper( final UpdateWorkflowResult result )
    {
        this.result = result;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "content", new ContentMapper( this.result.getContent() ) );
    }
}
