package com.enonic.wem.core.jcr.accounts;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

import com.enonic.wem.core.jcr.JcrInitializer;
import com.enonic.wem.core.jcr.JcrTemplate;
import com.enonic.wem.itest.AbstractSpringTest;

import static org.junit.Assert.*;

public class AccountDaoTest
    extends AbstractSpringTest

{
    private static final String CONFIG_XML =
        "<?xml version=\"1.0\"?><config><user-fields><prefix/><first-name/><middle-name/><last-name/><suffix/><initials/><nick-name/></user-fields></config>";

    @Autowired
    private AccountJcrDao accountJcrDao;

    @Autowired
    private JcrTemplate jcrTemplate;

    @Before
    public void setup()
    {
        final JcrInitializer jcrInitializer = new JcrInitializer();
        jcrInitializer.setJcrTemplate( jcrTemplate );
        jcrInitializer.setCompactNodeDefinitionFile( new ClassPathResource( "com/enonic/wem/core/jcr/cmstypes.cnd" ) );
        jcrInitializer.initializeJcrRepository();

    }

    @Test
    public void createUserStoreTest()
    {
        final JcrUserStore userStore = new JcrUserStore();
        userStore.setName( "enonic" );
        userStore.setConnectorName( "local" );
        userStore.setDefaultStore( true );
        userStore.setXmlConfig( CONFIG_XML );

        accountJcrDao.createUserStore( userStore );
        assertNotNull( userStore.getId() );

        final JcrUserStore userstoreRetrieved = accountJcrDao.findUserStoreByName( userStore.getName() );
        assertNotNull( userstoreRetrieved );
        assertNotSame( userStore.getId(), userstoreRetrieved.getId() );
        assertEquals( userStore.getName(), userstoreRetrieved.getName() );
        assertEquals( userStore.getConnectorName(), userstoreRetrieved.getConnectorName() );
        assertEquals( userStore.isDefaultStore(), userstoreRetrieved.isDefaultStore() );
        assertEquals( userStore.getXmlConfig(), userstoreRetrieved.getXmlConfig() );
    }

}
