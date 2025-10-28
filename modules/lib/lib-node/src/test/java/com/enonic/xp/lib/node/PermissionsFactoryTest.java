package com.enonic.xp.lib.node;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.enonic.xp.core.internal.json.JsonHelper;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class PermissionsFactoryTest
{
    @Test
    void empty()
    {
        final AccessControlList acl = create( "{}" );

        assertNotNull( acl );
        checkAllowed( acl, RoleKeys.ADMIN.toString(), Permission.READ, Permission.MODIFY, Permission.CREATE, Permission.DELETE,
                      Permission.PUBLISH );
        checkAllowed( acl, RoleKeys.EVERYONE.toString(), Permission.READ );
    }

    @Test
    void full()
    {
        final AccessControlList acl = create( " { \"_permissions\": [\n" +
                                                  "        {\n" +
                                                  "            \"principal\": \"user:system:anonymous\",\n" +
                                                  "            \"allow\": [\n" +
                                                  "                \"READ\"\n" +
                                                  "            ],\n" +
                                                  "            \"deny\": []\n" +
                                                  "        },\n" +
                                                  "        {\n" +
                                                  "            \"principal\": \"role:admin\",\n" +
                                                  "            \"allow\": [\n" +
                                                  "                \"READ\",\n" +
                                                  "                \"CREATE\",\n" +
                                                  "                \"MODIFY\",\n" +
                                                  "                \"DELETE\",\n" +
                                                  "                \"PUBLISH\",\n" +
                                                  "                \"READ_PERMISSIONS\",\n" +
                                                  "                \"WRITE_PERMISSIONS\"\n" +
                                                  "            ],\n" +
                                                  "            \"deny\": []\n" +
                                                  "        },\n" +
                                                  "        {\n" +
                                                  "            \"principal\": \"role:everyone\",\n" +
                                                  "            \"allow\": [\n" +
                                                  "                \"READ\"\n" +
                                                  "            ],\n" +
                                                  "            \"deny\": [" +
                                                  "               \"DELETE\"\n " +
                                                  "            ]" +
                                                  "        },\n" +
                                                  "        {\n" +
                                                  "            \"principal\": \"role:authenticated\",\n" +
                                                  "            \"deny\": [" +
                                                  "               \"DELETE\"\n " +
                                                  "            ]" +
                                                  "        }\n" +
                                                  "    ]" +
                                                  "}" );

        assertNotNull( acl );
        checkAllowed( acl, "role:everyone", Permission.READ );
        checkDenied( acl, "role:everyone", Permission.CREATE, Permission.DELETE, Permission.MODIFY, Permission.MODIFY );
        checkAllowed( acl, "user:system:anonymous", Permission.READ );
        checkDenied( acl, "user:system:anonymous", Permission.CREATE, Permission.DELETE, Permission.MODIFY, Permission.MODIFY );
        checkAllowed( acl, "role:admin", Permission.READ, Permission.MODIFY, Permission.CREATE, Permission.DELETE, Permission.PUBLISH );
        checkAllowed( acl, "role:authenticated", Permission.READ, Permission.MODIFY, Permission.CREATE, Permission.PUBLISH );
        checkDenied( acl, "role:authenticated", Permission.DELETE );
    }

    private void checkAllowed( final AccessControlList acl, final String principalKey, final Permission... allowed )
    {
        final AccessControlEntry principal = acl.getEntry( PrincipalKey.from( principalKey ) );
        if ( principal == null )
        {
            fail( "Missing entry for principal [" + principalKey + "]" );
        }

        Arrays.stream( allowed ).forEach( ( entry ) -> assertTrue( principal.isAllowed( entry ) , "Should allow [" + entry + "]") );
    }

    private void checkDenied( final AccessControlList acl, final String principalKey, final Permission... denied )
    {
        final AccessControlEntry principal = acl.getEntry( PrincipalKey.from( principalKey ) );
        if ( principal == null )
        {
            fail( "Missing entry for principal [" + principalKey + "]" );
        }
        Arrays.stream( denied ).forEach( ( entry ) -> assertTrue( principal.isDenied( entry ), "Should deny [" + entry + "]" ) );
    }


    private AccessControlList create( final String json )
    {
        final PropertyTree properties = PropertyTree.fromMap( JsonHelper.toMap( JsonHelper.from( json ) ) );

        return new PermissionsFactory( properties.getRoot().getSets( "_permissions" ) ).create();
    }

}
