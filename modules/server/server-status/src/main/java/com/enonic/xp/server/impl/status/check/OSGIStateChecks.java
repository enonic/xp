package com.enonic.xp.server.impl.status.check;

import java.util.Set;

public interface OSGIStateChecks
{
    Set<String> READY_SERVICE_NAMES = Set.of( "com.enonic.xp.export.ExportService", "com.enonic.xp.scheduler.SchedulerService",
                                              "com.enonic.xp.portal.websocket.WebSocketManager",
                                              "com.enonic.xp.task.TaskService",
                                              "com.enonic.xp.task.TaskDescriptorService", "com.enonic.xp.script.event.ScriptEventManager",
                                              "com.enonic.xp.security.SecurityService", "com.enonic.xp.portal.url.PortalUrlService",
                                              "com.enonic.xp.web.multipart.MultipartService", "com.enonic.xp.mail.MailService",
                                              "com.enonic.xp.i18n.LocaleService", "com.enonic.xp.portal.owasp.HtmlSanitizer",
                                              "com.enonic.xp.schema.content.ContentTypeService",
                                              "com.enonic.xp.admin.tool.AdminToolDescriptorService",
                                              "com.enonic.xp.admin.widget.WidgetDescriptorService",
                                              "com.enonic.xp.app.ApplicationService" );

    Set<String> LIVE_SERVICE_NAMES =
        Set.of( "org.elasticsearch.client.Client", "org.elasticsearch.client.AdminClient", "org.elasticsearch.client.ClusterAdminClient" );
}
