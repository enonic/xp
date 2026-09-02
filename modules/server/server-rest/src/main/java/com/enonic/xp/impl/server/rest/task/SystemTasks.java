package com.enonic.xp.impl.server.rest.task;

import com.enonic.xp.descriptor.DescriptorKey;

/**
 * Tasks of the system application. They run as cluster tasks - on whichever node picks them up - unlike the local
 * tasks that read or write this node's file system (dumps, exports).
 */
public final class SystemTasks
{
    public static final DescriptorKey SNAPSHOT = DescriptorKey.from( "com.enonic.xp.app.system:snapshot" );

    public static final DescriptorKey RESTORE = DescriptorKey.from( "com.enonic.xp.app.system:restore" );

    public static final DescriptorKey VACUUM = DescriptorKey.from( "com.enonic.xp.app.system:vacuum" );

    public static final DescriptorKey AUDIT_LOG_CLEANUP = DescriptorKey.from( "com.enonic.xp.app.system:audit-log-cleanup" );

    public static final DescriptorKey REINDEX = DescriptorKey.from( "com.enonic.xp.app.system:reindex" );

    public static final DescriptorKey PROJECT_SYNC = DescriptorKey.from( "com.enonic.xp.app.system:project-sync" );

    private SystemTasks()
    {
    }
}
