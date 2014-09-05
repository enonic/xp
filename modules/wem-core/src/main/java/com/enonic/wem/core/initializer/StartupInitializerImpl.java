package com.enonic.wem.core.initializer;

import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.CreateContentParams;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.core.index.Index;
import com.enonic.wem.core.index.IndexService;
import com.enonic.wem.core.lifecycle.LifecycleBean;
import com.enonic.wem.core.lifecycle.LifecycleStage;
import com.enonic.wem.core.schema.CoreSchemasProvider;

final class StartupInitializerImpl
    extends LifecycleBean
    implements StartupInitializer
{
    private final static Logger LOG = LoggerFactory.getLogger( StartupInitializerImpl.class );

    @Inject
    protected IndexService indexService;

    @Inject
    protected ContentService contentService;

    public StartupInitializerImpl()
    {
        super( LifecycleStage.L5 );
    }

    @Override
    protected void doStart()
        throws Exception
    {
        initialize( false );
    }

    @Override
    protected void doStop()
        throws Exception
    {
        // Do nothing
    }

    public void initialize( final boolean reinit )
        throws Exception
    {
        if ( reinit )
        {
            cleanupOldData();
        }
    }

    private void cleanupOldData()
    {
        LOG.info( "Recreating indexes..." );

        final Set<String> indicesNames = indexService.getAllIndicesNames();

        for ( final String indexName : indicesNames )
        {
            LOG.info( "Deleting index: " + indexName );
            this.indexService.deleteIndex( indexName );
        }

        this.indexService.createIndex( Index.WORKSPACE );
        this.indexService.createIndex( Index.VERSION );

        // TODO: This is a HACK! Should not be needed if we can create the workspaces upfront.
        createDummyContent();
    }

    private void createDummyContent()
    {
        final Context context = new Context( ContentConstants.WORKSPACE_STAGE );

        this.contentService.create( createFolder().
            name( "dummy" ).
            parent( ContentPath.ROOT ).
            displayName( "Dummy Folder" ), context ).getPath();
    }

    private CreateContentParams createFolder()
    {
        return new CreateContentParams().
            owner( AccountKey.anonymous() ).
            contentData( new ContentData() ).
            form( CoreSchemasProvider.FOLDER.form() ).
            contentType( ContentTypeName.folder() );
    }
}
