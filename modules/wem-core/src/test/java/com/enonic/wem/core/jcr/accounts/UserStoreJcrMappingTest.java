package com.enonic.wem.core.jcr.accounts;

import org.junit.Test;

import com.enonic.wem.core.jcr.JcrNode;
import com.enonic.wem.core.jcr.MockJcrNode;

import static org.junit.Assert.assertEquals;

public class UserStoreJcrMappingTest
{

    private static final String CONFIG_XML =
        "<?xml version=\"1.0\"?><config><user-fields><prefix/><first-name/><middle-name/><last-name/><suffix/><initials/><nick-name/></user-fields></config>";

    @Test
    public void toUserStoreTest()
    {

        final MockJcrNode userstoreNode = new MockJcrNode();
        userstoreNode.setPropertyString( "key", "d3ebbe0c-be4e-47ff-8c50-038732ae9a5a" );
        userstoreNode.setPropertyString( "name", "enonic" );
        userstoreNode.setPropertyBoolean( "default", true );
        userstoreNode.setPropertyString( "connector", "ldap" );
        userstoreNode.setPropertyString( "xmlConfig", CONFIG_XML );

        final UserStoreJcrMapping userStoreJcrMapping = new UserStoreJcrMapping();
        final JcrUserStore userStore = userStoreJcrMapping.toUserStore( userstoreNode );

        assertEquals( "d3ebbe0c-be4e-47ff-8c50-038732ae9a5a", userStore.getId() );
        assertEquals( "ldap", userStore.getConnectorName() );
        assertEquals( "enonic", userStore.getName() );
        assertEquals( CONFIG_XML, userStore.getXmlConfig() );
        assertEquals( true, userStore.isDefaultStore() );
    }

    @Test
    public void userStoreToJcrTest()
    {
        final JcrUserStore userStore = new JcrUserStore();
        userStore.setId( "d3ebbe0c-be4e-47ff-8c50-038732ae9a5a" );
        userStore.setName( "enonic" );
        userStore.setConnectorName( "ldap" );
        userStore.setXmlConfig( CONFIG_XML );
        userStore.setDefaultStore( true );

        final UserStoreJcrMapping userStoreJcrMapping = new UserStoreJcrMapping();
        final JcrNode userStoreNode = new MockJcrNode();
        userStoreJcrMapping.userStoreToJcr( userStore, userStoreNode );

        assertEquals( "d3ebbe0c-be4e-47ff-8c50-038732ae9a5a", userStoreNode.getPropertyString( "key" ) );
        assertEquals( "ldap", userStoreNode.getPropertyString( "connector" ) );
        assertEquals( "enonic", userStoreNode.getPropertyString( "name" ) );
        assertEquals( CONFIG_XML, userStoreNode.getPropertyString( "xmlConfig" ) );
        assertEquals( true, userStoreNode.getPropertyBoolean( "default" ) );
    }
}
