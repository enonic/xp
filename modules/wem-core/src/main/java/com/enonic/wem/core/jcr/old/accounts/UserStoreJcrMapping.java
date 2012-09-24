package com.enonic.wem.core.jcr.old.accounts;

import com.enonic.wem.core.jcr.old.JcrNode;

public class UserStoreJcrMapping
{

    static final String KEY = "key";

    static final String DEFAULT = "default";

    static final String CONNECTOR = "connector";

    static final String XML_CONFIG = "xmlConfig";

    static final String NAME = "name";


    public JcrUserStore toUserStore( JcrNode node )
    {
        final JcrUserStore userStore = new JcrUserStore();
        userStore.setName( node.getPropertyString( NAME ) );
        userStore.setId( node.getPropertyString( KEY ) );
        userStore.setDefaultStore( node.getPropertyBoolean( DEFAULT ) );
        userStore.setConnectorName( node.getPropertyString( CONNECTOR ) );
        userStore.setXmlConfig( node.getPropertyString( XML_CONFIG ) );
        return userStore;
    }

    public void userStoreToJcr( JcrUserStore userStore, JcrNode node )
    {
        node.setPropertyString( NAME, userStore.getName() );
        node.setPropertyString( KEY, userStore.getId() );
        node.setPropertyBoolean( DEFAULT, userStore.isDefaultStore() );
        node.setPropertyString( CONNECTOR, userStore.getConnectorName() );
        node.setPropertyString( XML_CONFIG, userStore.getXmlConfig() );
    }

}
