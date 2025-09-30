package com.enonic.xp.core.impl.content.page;


import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.core.impl.content.ContentServiceImpl;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageTemplate;
import com.enonic.xp.region.Regions;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeNames;
import com.enonic.xp.security.PrincipalKey;

class CreatePageTemplateCommand
{
    private ContentService contentService;

    private ContentPath site;

    private ContentName name;

    private String displayName;

    private DescriptorKey controller;

    private ContentTypeNames supports;

    private Regions regions;

    private PropertyTree pageConfig;

    public CreatePageTemplateCommand contentService( final ContentService contentService )
    {
        this.contentService = contentService;
        return this;
    }

    public CreatePageTemplateCommand site( final ContentPath site )
    {
        this.site = site;
        return this;
    }

    public CreatePageTemplateCommand name( final ContentName name )
    {
        this.name = name;
        return this;
    }

    public CreatePageTemplateCommand displayName( final String displayName )
    {
        this.displayName = displayName;
        return this;
    }

    public CreatePageTemplateCommand controller( final DescriptorKey controller )
    {
        this.controller = controller;
        return this;
    }

    public CreatePageTemplateCommand supports( final ContentTypeNames supports )
    {
        this.supports = supports;
        return this;
    }

    public CreatePageTemplateCommand regions( final Regions regions )
    {
        this.regions = regions;
        return this;
    }

    public CreatePageTemplateCommand pageConfig( final PropertyTree pageConfig )
    {
        this.pageConfig = pageConfig;
        return this;
    }

    public PageTemplate execute()
    {
        final PropertyTree data = new PropertyTree();
        new PageTemplateFormDataBuilder().supports( supports ).appendData( data.getRoot() );

        return (PageTemplate) contentService.create( CreateContentParams.create()
                                                         .name( name )
                                                         .displayName( displayName )
                                                         .owner( PrincipalKey.ofAnonymous() )
                                                         .contentData( data )
                                                         .type( ContentTypeName.pageTemplate() )
                                                         .parent( ContentPath.from( site, ContentServiceImpl.TEMPLATES_FOLDER_NAME ) )
                                                         .page( Page.create()
                                                                    .descriptor( controller )
                                                                    .config( pageConfig )
                                                                    .regions( regions )
                                                                    .build() )
                                                         .build() );
    }
}
