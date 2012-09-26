package com.enonic.wem.migrate.jcr;

import java.io.IOException;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.enonic.wem.core.jcr.old.JcrCallback;
import com.enonic.wem.core.jcr.old.JcrInitializer;
import com.enonic.wem.core.jcr.old.JcrSession;
import com.enonic.wem.core.jcr.old.JcrTemplate;
import com.enonic.wem.migrate.MigrateTask;

@Component
public class JcrMigrateTask
    implements MigrateTask
{
    private Resource compactNodeDefinitionFile;

    private JcrTemplate jcrTemplate;

    private JcrAccountsImporter jcrAccountsImporter;

    public JcrMigrateTask()
    {
    }

    @Override
    public void migrate()
        throws Exception
    {
        final JcrInitializer jcrInitializer = new JcrInitializer();
        jcrInitializer.setJcrTemplate( this.jcrTemplate );
        jcrInitializer.initializeJcrRepository();

        jcrTemplate.execute( new JcrCallback()
        {
            @Override
            public Object doInJcr( JcrSession session )
                throws IOException, RepositoryException
            {
                migrateToJcr( session.getRealSession() );
                return null;
            }
        } );
    }

    private void migrateToJcr( final Session session )
        throws RepositoryException, IOException
    {
        jcrAccountsImporter.importAccounts();
        session.save();
    }

    @Value("classpath:com/enonic/wem/core/jcr/old/cmstypes.cnd")
    public void setCompactNodeDefinitionFile( Resource compactNodeDefinitionFile )
    {
        this.compactNodeDefinitionFile = compactNodeDefinitionFile;
    }

    @Autowired
    public void setJcrTemplate( JcrTemplate jcrTemplate )
    {
        this.jcrTemplate = jcrTemplate;
    }

    @Autowired
    public void setJcrAccountsImporter( JcrAccountsImporter jcrAccountsImporter )
    {
        this.jcrAccountsImporter = jcrAccountsImporter;
    }
}
