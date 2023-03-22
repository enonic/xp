package com.enonic.xp.server.impl.status.check;

import java.util.List;

import org.osgi.framework.BundleContext;

public final class ReadyOSGIStateCheck
    extends OSGIStateCheck
{
    private static final List<String> TRACKED_SERVICE_NAMES =
        List.of( "com.enonic.xp.export.ExportService", "com.enonic.xp.scheduler.SchedulerService",
                 "com.enonic.xp.portal.websocket.WebSocketManager", "com.enonic.xp.portal.view.ViewFunctionService",
                 "com.enonic.xp.task.TaskService", "com.enonic.xp.task.TaskDescriptorService",
                 "com.enonic.xp.script.event.ScriptEventManager", "com.enonic.xp.security.SecurityService",
                 "com.enonic.xp.portal.url.PortalUrlService", "com.enonic.xp.web.multipart.MultipartService",
                 "com.enonic.xp.mail.MailService", "com.enonic.xp.i18n.LocaleService", "com.enonic.xp.portal.owasp.HtmlSanitizer",
                 "com.enonic.xp.schema.content.ContentTypeService", "com.enonic.xp.admin.tool.AdminToolDescriptorService",
                 "com.enonic.xp.server.internal.deploy.DeployDirectoryWatcher", "com.enonic.xp.server.internal.deploy.AutoDeployer",
                 "com.enonic.xp.server.internal.deploy.StoredApplicationsDeployer" );

    public ReadyOSGIStateCheck( final BundleContext bundleContext )
    {
        super( bundleContext );
    }

    @Override
    List<String> getServicesToTrack()
    {
        return TRACKED_SERVICE_NAMES;
    }
}
