package com.enonic.xp.lib.node.mapper;

import java.util.List;
import java.util.Map;

import com.enonic.xp.node.ApplyNodePermissionsResult;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

public class ApplyPermissionsResultMapper
    implements MapSerializable
{
    private final ApplyNodePermissionsResult result;


    public ApplyPermissionsResultMapper( final ApplyNodePermissionsResult result )
    {
        this.result = result;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        for ( Map.Entry<NodeId, List<ApplyNodePermissionsResult.BranchResult>> entry : result.getResults().entrySet() )
        {
            gen.map( entry.getKey().toString() );

            gen.array( "branchResults" );
            entry.getValue().forEach( branchResult -> {
                gen.map();
                gen.value( "branch", branchResult.branch() );
                serializePermissions( gen, branchResult.permissions() );
                gen.end();
            } );
            gen.end();
            gen.end();
        }
    }

    private void serializePermissions( final MapGenerator gen, final AccessControlList permissions )
    {
        gen.array( "permissions" );
        if ( permissions != null )
        {
            for ( AccessControlEntry entry : permissions )
            {
                gen.map();
                gen.value( "principal", entry.getPrincipal().toString() );

                gen.array( "allow" );
                for ( Permission permission : entry.getAllowedPermissions() )
                {
                    gen.value( permission.toString() );
                }
                gen.end();

                gen.array( "deny" );
                for ( Permission permission : entry.getDeniedPermissions() )
                {
                    gen.value( permission.toString() );
                }
                gen.end();

                gen.end();
            }
        }
        gen.end();
    }
}
