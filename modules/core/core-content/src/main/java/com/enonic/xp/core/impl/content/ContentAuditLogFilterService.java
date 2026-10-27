package com.enonic.xp.core.impl.content;

public interface ContentAuditLogFilterService
{
    boolean accept( String eventType );
}
