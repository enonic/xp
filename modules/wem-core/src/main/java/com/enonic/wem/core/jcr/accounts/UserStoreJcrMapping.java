package com.enonic.wem.core.jcr.accounts;

import com.enonic.wem.core.jcr.JcrNode;

public class UserStoreJcrMapping
{
    public JcrUserStore toUserStore( JcrNode node )
    {
        final JcrUserStore userStore = new JcrUserStore();
        userStore.setName( node.getName() );
        userStore.setId( node.getPropertyString( "key" ) );
        userStore.setDefaultStore( node.getPropertyBoolean( "default" ) );
        userStore.setConnectorName( node.getPropertyString( "connector" ) );
        userStore.setXmlConfig( node.getPropertyString( "xmlconfig" ) );
        return userStore;
    }

    public void userStoreToJcr( JcrUserStore userStore, JcrNode node )
    {
        node.setPropertyString( "key", userStore.getId() );
        node.setPropertyBoolean( "default", userStore.isDefaultStore() );
        node.setPropertyString( "connector", userStore.getConnectorName() );
        node.setPropertyString( "xmlconfig", userStore.getXmlConfig() );
    }

}
