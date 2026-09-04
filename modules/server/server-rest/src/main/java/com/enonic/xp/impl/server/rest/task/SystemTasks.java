package com.enonic.xp.impl.server.rest.task;

import com.enonic.xp.descriptor.DescriptorKey;

/**
 * Tasks of the system application. They run as cluster tasks, on whichever node picks them up; dumps and exports rely
 * on the data directory being shared between the nodes of a cluster.
 */
public final class SystemTasks
{
    public static final DescriptorKey SNAPSHOT = DescriptorKey.from( "com.enonic.xp.app.system:snapshot" );

    public static final DescriptorKey RESTORE = DescriptorKey.from( "com.enonic.xp.app.system:restore" );

    public static final DescriptorKey VACUUM = DescriptorKey.from( "com.enonic.xp.app.system:vacuum" );

    public static final DescriptorKey AUDIT_LOG_CLEANUP = DescriptorKey.from( "com.enonic.xp.app.system:audit-log-cleanup" );

    public static final DescriptorKey REINDEX = DescriptorKey.from( "com.enonic.xp.app.system:reindex" );

    public static final DescriptorKey DUMP = DescriptorKey.from( "com.enonic.xp.app.system:dump" );

    public static final DescriptorKey LOAD = DescriptorKey.from( "com.enonic.xp.app.system:load" );

    public static final DescriptorKey UPGRADE = DescriptorKey.from( "com.enonic.xp.app.system:upgrade" );

    public static final DescriptorKey EXPORT = DescriptorKey.from( "com.enonic.xp.app.system:export" );

    public static final DescriptorKey IMPORT = DescriptorKey.from( "com.enonic.xp.app.system:import" );

    private SystemTasks()
    {
    }
}
