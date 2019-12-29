package com.enonic.xp.core.impl.export.xml;

import java.io.InputStreamReader;
import java.io.Reader;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.xml.DomElement;
import com.enonic.xp.xml.DomHelper;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class XmlPermissionsParserTest
    extends BaseXmlSerializerTest
{

    @Test
    public void testParse()
        throws Exception
    {
        final Document doc;
        try (Reader reader = new InputStreamReader( getClass().getResourceAsStream( "permissions.xml" ) ))
        {
            doc = DomHelper.parse( reader );
        }

        final AccessControlList accessControlList = XmlPermissionsParser.parse( DomElement.from( doc.getDocumentElement() ) );

        final AccessControlEntry entry = accessControlList.getEntry( PrincipalKey.from( "role:system.admin" ) );
        assertNotNull( entry );
        assertTrue( entry.isAllowed( Permission.READ ) );
        assertTrue( entry.isAllowed( Permission.CREATE ) );
        assertTrue( entry.isAllowed( Permission.MODIFY ) );
        assertTrue( entry.isAllowed( Permission.DELETE ) );
        assertTrue( entry.isAllowed( Permission.PUBLISH ) );
        assertTrue( entry.isAllowed( Permission.READ_PERMISSIONS ) );
        assertTrue( entry.isAllowed( Permission.WRITE_PERMISSIONS ) );

        final AccessControlEntry cmsAdmin = accessControlList.getEntry( PrincipalKey.from( "role:cms.admin" ) );
        assertNotNull( cmsAdmin );
        assertTrue( cmsAdmin.isAllowed( Permission.CREATE ) );
        assertTrue( cmsAdmin.isDenied( Permission.PUBLISH ) );
    }
}
