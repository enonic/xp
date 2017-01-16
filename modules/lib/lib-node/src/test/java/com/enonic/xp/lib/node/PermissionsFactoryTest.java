package com.enonic.xp.lib.node;

import java.util.Arrays;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.lib.common.JsonToPropertyTreeTranslator;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.util.JsonHelper;

import static org.junit.Assert.*;

public class PermissionsFactoryTest
{
    @Test
    public void empty()
        throws Exception
    {
        final AccessControlList acl = create( "{}" );

        assertNotNull( acl );
        checkAllowed( acl, RoleKeys.ADMIN.toString(), Permission.READ, Permission.MODIFY, Permission.CREATE, Permission.DELETE,
                      Permission.PUBLISH );
        checkAllowed( acl, RoleKeys.EVERYONE.toString(), Permission.READ );
    }

    @Test
    public void full()
        throws Exception
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

        Arrays.stream( allowed ).forEach( ( entry ) -> assertTrue( "Should allow [" + entry + "]", principal.isAllowed( entry ) ) );
    }

    private void checkDenied( final AccessControlList acl, final String principalKey, final Permission... denied )
    {
        final AccessControlEntry principal = acl.getEntry( PrincipalKey.from( principalKey ) );
        if ( principal == null )
        {
            fail( "Missing entry for principal [" + principalKey + "]" );
        }
        Arrays.stream( denied ).forEach( ( entry ) -> assertTrue( "Should deny [" + entry + "]", principal.isDenied( entry ) ) );
    }


    private AccessControlList create( final String json )
    {
        final JsonNode node = JsonHelper.from( json );

        final PropertyTree properties = JsonToPropertyTreeTranslator.translate( node );

        return new PermissionsFactory( properties.getRoot().getSets( "_permissions" ) ).create();
    }

}