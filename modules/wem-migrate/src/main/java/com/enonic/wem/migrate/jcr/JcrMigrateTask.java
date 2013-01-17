package com.enonic.wem.migrate.jcr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.core.jcr.loader.JcrInitializer;
import com.enonic.wem.core.jcr.provider.JcrSessionProvider;
import com.enonic.wem.core.search.account.AccountSearchService;
import com.enonic.wem.migrate.MigrateTask;

@Component
public class JcrMigrateTask
    implements MigrateTask
{
    private static final Logger LOG = LoggerFactory.getLogger( JcrMigrateTask.class );

    private JcrSessionProvider jcrSessionProvider;

    private JcrAccountsImporter jcrAccountsImporter;

    @Override
    public void migrate()
        throws Exception
    {
        final JcrInitializer jcrInitializer = new JcrInitializer( jcrSessionProvider );

        if ( jcrInitializer.initialize() )
        {
            createAccountsIndex();

            LOG.info( "Importing accounts..." );
            jcrAccountsImporter.importAccounts();
            LOG.info( "Accounts imported to JCR." );
        }
        else
        {
            LOG.info( "JCR already initialized, skipping import of accounts." );
        }
    }

    public void createAccountsIndex()
    {
        LOG.info( "Creating index and mapping for accounts..." );
        // accountSearchService.dropIndex();
        // accountSearchService.createIndex();
        LOG.info( "Index and mapping created." );
    }

    @Autowired
    public void setJcrAccountsImporter( JcrAccountsImporter jcrAccountsImporter )
    {
        this.jcrAccountsImporter = jcrAccountsImporter;
    }

    @Autowired
    public void setJcrSessionProvider( final JcrSessionProvider jcrSessionProvider )
    {
        this.jcrSessionProvider = jcrSessionProvider;
    }
}
