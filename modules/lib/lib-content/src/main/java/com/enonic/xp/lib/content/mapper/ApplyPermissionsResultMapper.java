package com.enonic.xp.lib.content.mapper;

import java.util.Map;

import com.enonic.xp.content.ApplyContentPermissionsResult;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.security.acl.AccessControlList;

public class ApplyPermissionsResultMapper
    implements MapSerializable
{
    private final ApplyContentPermissionsResult result;


    public ApplyPermissionsResultMapper( final ApplyContentPermissionsResult result )
    {
        this.result = result;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        for ( Map.Entry<ContentId, AccessControlList> entry : result.getResults().entrySet() )
        {
            gen.map( entry.getKey().toString() );
            new PermissionsMapper( entry.getValue() ).serialize( gen );
            gen.end();
        }
    }
}
