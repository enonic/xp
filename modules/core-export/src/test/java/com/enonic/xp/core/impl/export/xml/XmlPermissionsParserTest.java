package com.enonic.xp.core.impl.export.xml;

import java.net.URL;

import org.junit.Test;
import org.w3c.dom.Document;

import com.google.common.base.Charsets;
import com.google.common.io.CharSource;
import com.google.common.io.Resources;

import com.enonic.xp.node.Node;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.xml.DomElement;
import com.enonic.xp.xml.DomHelper;

import static org.junit.Assert.*;

public class XmlPermissionsParserTest
    extends BaseXmlSerializerTest
{

    @Test
    public void testParse()
        throws Exception
    {
        final Node.Builder builder = Node.create();

        final XmlPermissionsParser parser = new XmlPermissionsParser();
        final URL resource = getClass().getResource( "permissions.xml" );
        final CharSource charSource = Resources.asCharSource( resource, Charsets.UTF_8 );
        final Document doc = DomHelper.parse( charSource.openStream() );

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