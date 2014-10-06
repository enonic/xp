package com.enonic.wem.core.content.page;


import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentName;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.CreateContentParams;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.page.CreatePageParams;
import com.enonic.wem.api.content.page.PageDescriptorKey;
import com.enonic.wem.api.content.page.PageRegions;
import com.enonic.wem.api.content.page.PageService;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateFormDataBuilder;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.schema.content.ContentTypeForms;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;

class CreatePageTemplateCommand
{
    private ContentService contentService;

    private PageService pageService;

    private ContentPath site;

    private ContentName name;

    private String displayName;

    private PageDescriptorKey controller;

    private ContentTypeNames supports;

    private PageRegions pageRegions;

    private RootDataSet pageConfig;

    public CreatePageTemplateCommand contentService( final ContentService contentService )
    {
        this.contentService = contentService;
        return this;
    }

    public CreatePageTemplateCommand pageService( final PageService pageService )
    {
        this.pageService = pageService;
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

    public CreatePageTemplateCommand controller( final PageDescriptorKey controller )
    {
        this.controller = controller;
        return this;
    }

    public CreatePageTemplateCommand supports( final ContentTypeNames supports )
    {
        this.supports = supports;
        return this;
    }

    public CreatePageTemplateCommand pageRegions( final PageRegions pageRegions )
    {
        this.pageRegions = pageRegions;
        return this;
    }

    public CreatePageTemplateCommand pageConfig( final RootDataSet pageConfig )
    {
        this.pageConfig = pageConfig;
        return this;
    }

    public PageTemplate execute()
    {
        final ContentData data = new ContentData();
        new PageTemplateFormDataBuilder().
            controller( controller ).
            supports( supports ).
            appendData( data );

        final Content content = contentService.create( new CreateContentParams().
            name( name ).
            displayName( displayName ).
            owner( AccountKey.anonymous() ).
            contentData( data ).
            form( ContentTypeForms.PAGE_TEMPLATE ).
            contentType( ContentTypeName.pageTemplate() ).
            parent( ContentPath.from( site, "templates" ) ) );

        return (PageTemplate) pageService.create( new CreatePageParams().
            content( content.getId() ).
            config( pageConfig ).
            regions( pageRegions ) );
    }
}
