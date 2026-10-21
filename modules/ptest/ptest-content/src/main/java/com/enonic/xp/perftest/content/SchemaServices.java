package com.enonic.xp.perftest.content;

import com.enonic.xp.core.impl.app.ApplicationFactoryService;
import com.enonic.xp.core.impl.app.ApplicationRegistry;
import com.enonic.xp.core.impl.app.DynamicSchemaAuditLogSupportImpl;
import com.enonic.xp.core.impl.app.DynamicSchemaServiceImpl;
import com.enonic.xp.core.impl.app.resource.ResourceServiceImpl;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.resource.DynamicSchemaService;
import com.enonic.xp.resource.ResourceService;

/**
 * The resource and schema services as wired by the version of XP being measured: the only code of {@link SchemaLookupBenchmark}
 * that differs between the version without the schema cache and the version with it.
 */
final class SchemaServices
{
    final ResourceService resourceService;

    final DynamicSchemaService dynamicSchemaService;

    SchemaServices( final ApplicationFactoryService applicationFactoryService, final ApplicationRegistry applicationRegistry,
                    final NodeService nodeService, final DynamicSchemaAuditLogSupportImpl auditLogSupport,
                    final EventPublisher eventPublisher )
    {
        this.resourceService = new ResourceServiceImpl( applicationFactoryService );
        this.dynamicSchemaService = new DynamicSchemaServiceImpl( nodeService, auditLogSupport, eventPublisher, applicationRegistry );
    }
}
