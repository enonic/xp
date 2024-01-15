package com.enonic.xp.server.internal.deploy;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.condition.Condition;

import com.enonic.xp.admin.tool.AdminToolDescriptorService;
import com.enonic.xp.core.internal.Dictionaries;
import com.enonic.xp.export.ExportService;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.mail.MailService;
import com.enonic.xp.portal.owasp.HtmlSanitizer;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.view.ViewFunctionService;
import com.enonic.xp.portal.websocket.WebSocketManager;
import com.enonic.xp.scheduler.SchedulerService;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.script.event.ScriptEventManager;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.task.TaskDescriptorService;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.web.multipart.MultipartService;

@Component(immediate = true)
public class ApplicationDeployerManager
{
    private StoredApplicationsDeployer storedApplicationsDeployer;

    private AutoDeployer autoDeployer;

    private DeployDirectoryWatcher deployDirectoryWatcher;

    @Activate
    public void activate( final BundleContext bundleContext )
        throws Exception
    {
        bundleContext.registerService( Condition.class, Condition.INSTANCE,
                                       Dictionaries.of( Condition.CONDITION_ID, "com.enonic.xp.server.deploy.ready" ) );
        storedApplicationsDeployer.deploy();
        autoDeployer.deploy();
        deployDirectoryWatcher.deploy();
    }

    @Reference
    public void setStoredApplicationsDeployer( final StoredApplicationsDeployer storedApplicationsDeployer )
    {
        this.storedApplicationsDeployer = storedApplicationsDeployer;
    }

    @Reference
    public void setAutoDeployer( final AutoDeployer autoDeployer )
    {
        this.autoDeployer = autoDeployer;
    }

    @Reference
    public void setDeployDirectoryWatcher( final DeployDirectoryWatcher deployDirectoryWatcher )
    {
        this.deployDirectoryWatcher = deployDirectoryWatcher;
    }

    //TODO Temporary fix. See issue xp#7003
    @Reference
    public void setAdminToolDescriptorService( final AdminToolDescriptorService adminToolDescriptorService )
    {
        //Needed to ensure startup-order
    }

    @Reference
    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        //Needed to ensure startup-order
    }

    @Reference
    public void setHtmlSanitizer( final HtmlSanitizer htmlSanitizer )
    {
        //Needed to ensure startup-order
    }

    @Reference
    public void setLocaleService( final LocaleService localeService )
    {
        //Needed to ensure startup-order
    }

    @Reference
    public void setMailService( final MailService mailService )
    {
        //Needed to ensure startup-order
    }

    @Reference
    public void setMultipartService( final MultipartService multipartService )
    {
        //Needed to ensure startup-order
    }

    @Reference
    public void setPortalUrlService( final PortalUrlService portalUrlService )
    {
        //Needed to ensure startup-order
    }

    @Reference
    public void setSecurityService( final SecurityService securityService )
    {
        //Needed to ensure startup-order
    }

    @Reference
    public void setScriptEventManager( final ScriptEventManager scriptEventManager )
    {
        //Needed to ensure startup-order
    }

    @Reference
    public void setTaskDescriptorService( final TaskDescriptorService taskDescriptorService )
    {
        //Needed to ensure startup-order
    }

    @Reference
    public void setTaskService( final TaskService taskService )
    {
        //Needed to ensure startup-order
    }

    @Reference
    public void setViewFunctionService( final ViewFunctionService viewFunctionService )
    {
        //Needed to ensure startup-order
    }

    @Reference
    public void setWebSocketManager( final WebSocketManager webSocketManager )
    {
        //Needed to ensure startup-order
    }

    @Reference
    public void setSchedulerService( final SchedulerService schedulerService )
    {
        //Needed to ensure startup-order
    }

    @Reference
    public void setExportService( final ExportService exportService )
    {
        //Needed to ensure startup-order
    }

}
