package com.enonic.xp.core.impl.site;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.site.CreateSiteParams;
import com.enonic.xp.content.site.ModuleConfigsDataSerializer;
import com.enonic.xp.content.site.Site;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;

@Component(immediate = true)
public class SiteServiceImpl
    implements SiteService
{
    private static final ModuleConfigsDataSerializer MODULE_CONFIGS_DATA_SERIALIZER = new ModuleConfigsDataSerializer();

    private SiteDescriptorRegistry siteDescriptorRegistry;

    private ContentService contentService;

    @Override
    public SiteDescriptor getDescriptor( final ModuleKey moduleKey )
    {
        final SiteDescriptor siteDescriptor = this.siteDescriptorRegistry.get( moduleKey );
        if ( siteDescriptor == null )
        {
            return null;
        }
        return siteDescriptor;
    }

    @Override
    public Site create( final CreateSiteParams params )
    {
        final PropertyTree data = new PropertyTree();
        data.setString( "description", params.getDescription() );

        MODULE_CONFIGS_DATA_SERIALIZER.toProperties( params.getModuleConfigs(), data.getRoot() );

        final CreateContentParams createContentParams = CreateContentParams.create().
            type( ContentTypeName.site() ).
            parent( params.getParentContentPath() ).
            name( params.getName() ).
            displayName( params.getDisplayName() ).
            contentData( data ).
            requireValid( params.isRequireValid() ).
            build();

        return (Site) contentService.create( createContentParams );
    }

    @Override
    public Site getNearestSite( final ContentId contentId )
    {
        return GetNearestSiteCommand.create().
            contentService( contentService ).
            contentId( contentId ).
            build().
            execute();
    }

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    @Reference
    public void setSiteDescriptorRegistry( final SiteDescriptorRegistry siteDescriptorRegistry )
    {
        this.siteDescriptorRegistry = siteDescriptorRegistry;
    }
}
