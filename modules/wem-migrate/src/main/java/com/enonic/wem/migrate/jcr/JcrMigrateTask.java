package com.enonic.wem.migrate.jcr;

import javax.jcr.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.core.jcr.loader.JcrInitializer;
import com.enonic.wem.core.jcr.provider.JcrSessionProvider;
import com.enonic.wem.migrate.MigrateTask;

@Component
public class JcrMigrateTask
    implements MigrateTask
{
    private JcrSessionProvider jcrSessionProvider;

    private JcrAccountsImporter jcrAccountsImporter;

    public JcrMigrateTask()
    {
    }

    @Override
    public void migrate()
        throws Exception
    {
        final JcrInitializer jcrInitializer = new JcrInitializer(jcrSessionProvider);
        jcrInitializer.initialize();

        final Session session = jcrSessionProvider.loginAdmin();
        jcrAccountsImporter.importAccounts(session);
        session.save();
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
