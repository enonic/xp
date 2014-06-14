package com.enonic.wem.core;

import com.google.inject.AbstractModule;

import com.enonic.wem.core.blob.BlobModule;
import com.enonic.wem.core.blobstore.BlobStoreModule;
import com.enonic.wem.core.config.ConfigModule;
import com.enonic.wem.core.content.ContentModule;
import com.enonic.wem.core.elasticsearch.ElasticsearchModule;
import com.enonic.wem.core.entity.EntityModule;
import com.enonic.wem.core.home.HomeModule;
import com.enonic.wem.core.image.ImageModule;
import com.enonic.wem.core.index.IndexModule;
import com.enonic.wem.core.initializer.InitializerModule;
import com.enonic.wem.core.lifecycle.LifecycleModule;
import com.enonic.wem.core.module.ModuleModule;
import com.enonic.wem.core.relationship.RelationshipModule;
import com.enonic.wem.core.resource.ResourceModule;
import com.enonic.wem.core.schema.SchemaModule;
import com.enonic.wem.core.version.VersionModule;
import com.enonic.wem.core.workspace.WorkspaceModule;

public final class CoreModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        install( new HomeModule() );
        install( new ConfigModule() );
        install( new InitializerModule() );
        install( new BlobStoreModule() );
        install( new EntityModule() );
        install( new BlobModule() );
        install( new ContentModule() );
        install( new RelationshipModule() );
        install( new SchemaModule() );
        install( new ElasticsearchModule() );
        install( new IndexModule() );
        install( new ModuleModule() );
        install( new ResourceModule() );
        install( new LifecycleModule() );
        install( new ImageModule() );
        install( new WorkspaceModule() );
        install( new VersionModule() );
    }
}
