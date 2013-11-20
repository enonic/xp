package com.enonic.wem.core;

import com.google.inject.AbstractModule;

import com.enonic.wem.core.account.AccountModule;
import com.enonic.wem.core.blobstore.BlobStoreModule;
import com.enonic.wem.core.client.ClientModule;
import com.enonic.wem.core.config.ConfigModule;
import com.enonic.wem.core.content.ContentModule;
import com.enonic.wem.core.content.site.SiteModule;
import com.enonic.wem.core.entity.EntityModule;
import com.enonic.wem.core.event.EventModule;
import com.enonic.wem.core.hazelcast.HazelcastModule;
import com.enonic.wem.core.home.HomeModule;
import com.enonic.wem.core.index.IndexModule;
import com.enonic.wem.core.initializer.InitializerModule;
import com.enonic.wem.core.jcr.JcrModule;
import com.enonic.wem.core.module.ModuleModule;
import com.enonic.wem.core.relationship.RelationshipModule;
import com.enonic.wem.core.resource.ResourceModule;
import com.enonic.wem.core.schema.SchemaModule;
import com.enonic.wem.core.servlet.ServletModule;
import com.enonic.wem.core.space.SpaceModule;
import com.enonic.wem.core.userstore.UserStoreModule;
import com.enonic.wem.migrate.MigrateModule;

public final class CoreModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        install( new ServletModule() );
        install( new EventModule() );

        install( new HomeModule() );
        install( new ConfigModule() );
        install( new JcrModule() );
        install( new InitializerModule() );
        install( new ClientModule() );
        install( new BlobStoreModule() );
        install( new EntityModule() );
        install( new AccountModule() );
        install( new ContentModule() );
        install( new SiteModule() );
        install( new RelationshipModule() );
        install( new SchemaModule() );
        install( new IndexModule() );
        install( new SpaceModule() );
        install( new UserStoreModule() );
        install( new ResourceModule() );
        install( new ModuleModule() );

        install( new HazelcastModule() );

        // TODO: Move to plugin. Need some service starting system first.
        install( new MigrateModule() );
    }
}
