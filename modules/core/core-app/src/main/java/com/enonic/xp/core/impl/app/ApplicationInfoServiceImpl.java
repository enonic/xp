package com.enonic.xp.core.impl.app;

import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationInfo;
import com.enonic.xp.app.ApplicationInfoService;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Contents;
import com.enonic.xp.descriptor.Descriptors;
import com.enonic.xp.idprovider.IdProviderDescriptor;
import com.enonic.xp.idprovider.IdProviderDescriptorService;
import com.enonic.xp.macro.MacroDescriptorService;
import com.enonic.xp.macro.MacroDescriptors;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.page.PageDescriptors;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.LayoutDescriptors;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.region.PartDescriptors;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.ContentTypes;
import com.enonic.xp.schema.relationship.RelationshipTypeService;
import com.enonic.xp.schema.relationship.RelationshipTypes;
import com.enonic.xp.security.IdProviders;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.task.TaskDescriptor;
import com.enonic.xp.task.TaskDescriptorService;

@Component
public final class ApplicationInfoServiceImpl
    implements ApplicationInfoService
{
    private PageDescriptorService pageDescriptorService;

    private PartDescriptorService partDescriptorService;

    private RelationshipTypeService relationshipTypeService;

    private LayoutDescriptorService layoutDescriptorService;

    private MacroDescriptorService macroDescriptorService;

    private ContentTypeService contentTypeService;

    private ContentService contentService;

    private ResourceService resourceService;

    private TaskDescriptorService taskDescriptorService;

    private SecurityService securityService;

    private IdProviderDescriptorService idProviderDescriptorService;

    @Override
    public ContentTypes getContentTypes( final ApplicationKey applicationKey )
    {
        return contentTypeService.getByApplication( applicationKey );
    }

    @Override
    public PageDescriptors getPageDescriptors( final ApplicationKey applicationKey )
    {
        return this.pageDescriptorService.getByApplication( applicationKey );
    }

    @Override
    public PartDescriptors getPartDescriptors( final ApplicationKey applicationKey )
    {
        return this.partDescriptorService.getByApplication( applicationKey );
    }

    @Override
    public LayoutDescriptors getLayoutDescriptors( final ApplicationKey applicationKey )
    {
        return this.layoutDescriptorService.getByApplication( applicationKey );
    }

    @Override
    public RelationshipTypes getRelationshipTypes( final ApplicationKey applicationKey )
    {
        return this.relationshipTypeService.getByApplication( applicationKey );
    }

    @Override
    public MacroDescriptors getMacroDescriptors( final ApplicationKey applicationKey )
    {
        return this.macroDescriptorService.getByApplications( ApplicationKeys.from( applicationKey, ApplicationKey.SYSTEM ) );
    }

    @Override
    public Descriptors<TaskDescriptor> getTaskDescriptors( final ApplicationKey applicationKey )
    {
        return this.taskDescriptorService.getTasks( applicationKey );
    }

    @Override
    @Deprecated
    public Contents getContentReferences( final ApplicationKey applicationKey )
    {
        return Contents.empty();
    }

    @Override
    public IdProviders getIdProviderReferences( final ApplicationKey applicationKey )
    {
        return IdProviders.from( securityService.getIdProviders().
            stream().
            filter( idProvider -> idProvider.getIdProviderConfig() != null &&
                idProvider.getIdProviderConfig().getApplicationKey().equals( applicationKey ) ).collect( Collectors.toList() ) );

    }

    @Override
    public IdProviderDescriptor getIdProviderDescriptor( final ApplicationKey applicationKey )
    {
        return this.idProviderDescriptorService.getDescriptor( applicationKey );
    }

    @Override
    public ApplicationInfo getApplicationInfo( final ApplicationKey applicationKey )
    {
        return ApplicationInfo.create().
            setContentTypes( this.getContentTypes( applicationKey ) ).
            setPages( this.getPageDescriptors( applicationKey ) ).
            setParts( this.getPartDescriptors( applicationKey ) ).
            setLayouts( this.getLayoutDescriptors( applicationKey ) ).
            setRelations( this.getRelationshipTypes( applicationKey ) ).
            setContentReferences( this.getContentReferences( applicationKey ) ).
            setIdProviderReferences( this.getIdProviderReferences( applicationKey ) ).
            setMacros( this.getMacroDescriptors( applicationKey ) ).
            setTasks( this.getTaskDescriptors( applicationKey ) ).
            setIdProviderDescriptor( this.getIdProviderDescriptor( applicationKey ) ).
            build();
    }

    @Reference
    public void setPageDescriptorService( final PageDescriptorService pageDescriptorService )
    {
        this.pageDescriptorService = pageDescriptorService;
    }

    @Reference
    public void setPartDescriptorService( final PartDescriptorService partDescriptorService )
    {
        this.partDescriptorService = partDescriptorService;
    }

    @Reference
    public void setLayoutDescriptorService( final LayoutDescriptorService layoutDescriptorService )
    {
        this.layoutDescriptorService = layoutDescriptorService;
    }

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    @Reference
    public void setResourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }

    @Reference
    public void setTaskDescriptorService( final TaskDescriptorService taskDescriptorService )
    {
        this.taskDescriptorService = taskDescriptorService;
    }

    @Reference
    public void setSecurityService( final SecurityService securityService )
    {
        this.securityService = securityService;
    }

    @Reference
    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
    }

    @Reference
    public void setMacroDescriptorService( final MacroDescriptorService macroDescriptorService )
    {
        this.macroDescriptorService = macroDescriptorService;
    }

    @Reference
    public void setRelationshipTypeService( final RelationshipTypeService relationshipTypeService )
    {
        this.relationshipTypeService = relationshipTypeService;
    }

    @Reference
    public void setIdProviderDescriptorService( final IdProviderDescriptorService idProviderDescriptorService )
    {
        this.idProviderDescriptorService = idProviderDescriptorService;
    }

}
