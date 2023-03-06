package com.enonic.xp.core.impl.content;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.enonic.xp.core.impl.content.filter.ContentAuditLogFilter;

@Component(configurationPid = "com.enonic.xp.content")
public class ContentAuditLogFilterServiceImpl
    implements ContentAuditLogFilterService
{
    private final ContentAuditLogFilter contentAuditLogFilter;

    @Activate
    public ContentAuditLogFilterServiceImpl( final ContentConfig config )
    {
        contentAuditLogFilter = new ContentAuditLogFilter( config.auditlog_filter() );
    }

    @Override
    public boolean accept( final String eventType )
    {
        return contentAuditLogFilter.accept( eventType );
    }
}
