package com.enonic.wem.core.account.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import com.google.common.base.Strings;

import com.enonic.wem.api.userstore.UserStore;
import com.enonic.wem.api.userstore.UserStoreName;
import com.enonic.wem.api.userstore.config.UserStoreConfig;
import com.enonic.wem.api.userstore.config.UserStoreConfigParser;
import com.enonic.wem.api.userstore.config.UserStoreConfigSerializer;

class UserStoreJcrMapping
{
    static final String DEFAULT = "default";

    static final String CONNECTOR = "connector";

    static final String XML_CONFIG = "xmlConfig";

    private final UserStoreConfigSerializer userStoreConfigSerializer;

    private final UserStoreConfigParser userStoreConfigParser;

    UserStoreJcrMapping()
    {
        userStoreConfigSerializer = new UserStoreConfigSerializer();
        userStoreConfigParser = new UserStoreConfigParser();
    }

    public UserStore toUserStore( final Node node )
        throws Exception
    {
        final String name = node.getName();
        final UserStore userStore = new UserStore( UserStoreName.from( name ) );
        userStore.setDefaultStore( node.getProperty( DEFAULT ).getBoolean() );
        userStore.setConnectorName( node.getProperty( CONNECTOR ).getString() );
        final String xmlConfig = node.getProperty( XML_CONFIG ).getString();
        final UserStoreConfig config;
        if ( Strings.isNullOrEmpty( xmlConfig ) )
        {
            config = new UserStoreConfig();
        }
        else
        {
            config = userStoreConfigParser.parseXml( xmlConfig );
        }
        userStore.setConfig( config );
        return userStore;
    }

    public void userStoreToJcr( final UserStore userStore, final Node node )
        throws RepositoryException
    {
        node.setProperty( DEFAULT, userStore.isDefaultStore() );
        node.setProperty( CONNECTOR, userStore.getConnectorName() );
        node.setProperty( XML_CONFIG, userStoreConfigSerializer.toXmlString( userStore.getConfig() ) );
    }

}
