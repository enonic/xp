package com.enonic.xp.core.content;

import java.time.Instant;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.audit.LogAuditLogParams;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentDataValidationException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.WorkflowCheckState;
import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.content.WorkflowState;
import com.enonic.xp.core.impl.content.XDataMappingServiceImpl;
import com.enonic.xp.core.impl.schema.xdata.XDataServiceImpl;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.region.RegionDescriptors;
import com.enonic.xp.region.Regions;
import com.enonic.xp.resource.ResourceProcessor;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.xdata.XData;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.SiteConfigsDataSerializer;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.XDataMapping;
import com.enonic.xp.site.XDataMappings;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ContentServiceImplTest_create
    extends AbstractContentServiceTest
{

    @Test
    void create_content_generated_properties()
    {
        final CreateContentParams createContentParams = CreateContentParams.create()
            .contentData( new PropertyTree() )
            .displayName( "This is my content" )
            .parent( ContentPath.ROOT )
            .type( ContentTypeName.folder() )
            .build();

        final Content content = this.contentService.create( createContentParams );

        assertNotNull( content.getName() );
        assertEquals( "this-is-my-content", content.getName().toString() );
        assertNotNull( content.getCreatedTime() );
        assertNotNull( content.getCreator() );
        assertNotNull( content.getModifiedTime() );
        assertNotNull( content.getModifier() );
        assertNotNull( content.getChildOrder() );
        assertEquals( ContentConstants.DEFAULT_CHILD_ORDER, content.getChildOrder() );

        final Content storedContent = this.contentService.getById( content.getId() );

        assertNotNull( storedContent.getName() );
        assertEquals( "this-is-my-content", storedContent.getName().toString() );
        assertNotNull( storedContent.getCreatedTime() );
        assertNotNull( storedContent.getCreator() );
        assertNotNull( storedContent.getModifiedTime() );
        assertNotNull( storedContent.getModifier() );
        assertNotNull( storedContent.getChildOrder() );
        assertEquals( ContentConstants.DEFAULT_CHILD_ORDER, storedContent.getChildOrder() );
    }

    @Test
    void create_content_unnamed()
    {
        final CreateContentParams createContentParams = CreateContentParams.create()
            .contentData( new PropertyTree() )
            .parent( ContentPath.ROOT )
            .type( ContentTypeName.folder() )
            .build();

        final Content content = this.contentService.create( createContentParams );

        final Content storedContent = this.contentService.getById( content.getId() );

        assertNotNull( storedContent.getName() );
        assertTrue( storedContent.getName().isUnnamed() );
        assertTrue( storedContent.getName().hasUniqueness() );
        assertNotNull( storedContent.getCreatedTime() );
        assertNotNull( storedContent.getCreator() );
        assertNotNull( storedContent.getModifiedTime() );
        assertNotNull( storedContent.getModifier() );
        assertNotNull( storedContent.getChildOrder() );
        assertEquals( ContentConstants.DEFAULT_CHILD_ORDER, storedContent.getChildOrder() );
    }

    @Test
    void create_with_attachments()
    {
        xDataService = new XDataServiceImpl( mock( ApplicationService.class ), resourceService );
        xDataMappingService = new XDataMappingServiceImpl( siteService, xDataService );
        contentService.setxDataService( xDataService );
        contentService.setXDataMappingService( xDataMappingService );

        final String name = "cat-small.jpg";
        final ByteSource image = loadImage( name );

        final CreateContentParams createContentParams = CreateContentParams.create()
            .contentData( new PropertyTree() )
            .displayName( "This is my content" )
            .parent( ContentPath.ROOT )
            .type( ContentTypeName.imageMedia() )
            .createAttachments( createAttachment( "cat", "image/jpeg", image ) )
            .build();

        final Content content = this.contentService.create( createContentParams );

        final Content storedContent = this.contentService.getById( content.getId() );

        final Attachments attachments = storedContent.getAttachments();
        assertEquals( 1, attachments.getSize() );
    }

    @Test
    void create_with_root_language()
    {
        final Content root = this.contentService.getByPath( ContentPath.ROOT );
        contentService.update( new UpdateContentParams().contentId( root.getId() ).editor( edit -> edit.language = Locale.ENGLISH ) );

        final CreateContentParams createContentParams = CreateContentParams.create()
            .contentData( new PropertyTree() )
            .displayName( "This is my content" )
            .parent( ContentPath.ROOT )
            .type( ContentTypeName.folder() )
            .build();

        final Content content = this.contentService.create( createContentParams );

        final Content storedContent = this.contentService.getById( content.getId() );

        assertEquals( Locale.ENGLISH, storedContent.getLanguage() );
    }

    @Test
    void create_incorrect_content()
    {
        final PropertyTree contentData = new PropertyTree();
        contentData.addString( "target", "aStringValue" );

        final CreateContentParams createContentParams = CreateContentParams.create()
            .contentData( contentData )
            .displayName( "This is my shortcut" )
            .parent( ContentPath.ROOT )
            .type( ContentTypeName.shortcut() )
            .build();

        assertThrows( IllegalArgumentException.class, () -> {
            this.contentService.create( createContentParams );
        } );
    }

    @Test
    void create_with_publish_info()
    {
        final CreateContentParams createContentParams = CreateContentParams.create()
            .contentData( new PropertyTree() )
            .displayName( "This is my content" )
            .parent( ContentPath.ROOT )
            .type( ContentTypeName.folder() )
            .contentPublishInfo( ContentPublishInfo.create()
                                     .from( Instant.parse( "2016-11-03T10:42:00Z" ) )
                                     .from( Instant.parse( "2016-11-23T10:42:00Z" ) )
                                     .build() )
            .build();

        final Content content = this.contentService.create( createContentParams );
        assertNotNull( content.getPublishInfo() );
        assertNotNull( content.getPublishInfo().getFrom() );

        final Content storedContent = this.contentService.getById( content.getId() );
        assertNotNull( storedContent.getPublishInfo() );
        assertNotNull( storedContent.getPublishInfo().getFrom() );
    }

    @Test
    void create_with_workflow_info()
    {
        final CreateContentParams createContentParams = CreateContentParams.create()
            .contentData( new PropertyTree() )
            .displayName( "This is my content" )
            .parent( ContentPath.ROOT )
            .type( ContentTypeName.folder() )
            .workflowInfo( WorkflowInfo.create()
                               .state( WorkflowState.PENDING_APPROVAL )
                               .checks( Map.of( "My check", WorkflowCheckState.REJECTED ) )
                               .build() )
            .build();

        final Content content = this.contentService.create( createContentParams );
        assertNotNull( content.getWorkflowInfo() );
        assertEquals( WorkflowState.PENDING_APPROVAL, content.getWorkflowInfo().getState() );
        assertEquals( Map.of( "My check", WorkflowCheckState.REJECTED ), content.getWorkflowInfo().getChecks() );

        final Content storedContent = this.contentService.getById( content.getId() );
        assertNotNull( storedContent.getWorkflowInfo() );
        assertEquals( WorkflowState.PENDING_APPROVAL, storedContent.getWorkflowInfo().getState() );
        assertEquals( Map.of( "My check", WorkflowCheckState.REJECTED ), storedContent.getWorkflowInfo().getChecks() );
    }

    @Test
    void create_with_page()
    {
        final PropertyTree config = new PropertyTree();
        config.addString( "some", "line" );

        final Form pageDescriptorForm = Form.create()
            .addFormItem( Input.create().inputType( InputTypeName.TEXT_LINE ).name( "some" ).label( "label" ).build() )
            .build();

        final DescriptorKey pageDescriptorKey = DescriptorKey.from( "abc:abc" );

        Mockito.when( pageDescriptorService.getByKey( pageDescriptorKey ) )
            .thenReturn( PageDescriptor.create()
                             .displayName( "Landing page" )
                             .config( pageDescriptorForm )
                             .regions( RegionDescriptors.create().build() )
                             .key( DescriptorKey.from( "module:landing-page" ) )
                             .build() );

        final Page page = Page.create().descriptor( pageDescriptorKey ).config( config ).regions( Regions.create().build() ).build();

        final PropertyTree contentData = new PropertyTree();
        contentData.addString( "title", "This is my page" );

        final CreateContentParams createContentParams = CreateContentParams.create()
            .contentData( contentData )
            .displayName( "This is my page" )
            .parent( ContentPath.ROOT )
            .type( ContentTypeName.site() )
            .page( page )
            .build();

        final Content content = this.contentService.create( createContentParams );
        assertNotNull( content.getPage() );
        assertEquals( "abc:abc", content.getPage().getDescriptor().toString() );
        assertEquals( "line", content.getPage().getConfig().getString( "some" ) );
    }

    @Test
    void create_with_site_config()
    {
        final Form siteForm = Form.create()
            .addFormItem( Input.create().inputType( InputTypeName.TEXT_LINE ).name( "some" ).label( "label" ).build() )
            .build();

        final ApplicationKey appKey = ApplicationKey.from( "abc:abc" );

        Mockito.when( resourceService.processResource( isA( ResourceProcessor.class ) ) )
            .thenReturn( SiteDescriptor.create().applicationKey( appKey ).form( siteForm ).build() );

        final PropertyTree contentData = new PropertyTree();
        contentData.addString( "title", "This is my page" );
        final PropertySet siteConfig = contentData.addSet( ContentPropertyNames.SITECONFIG );
        siteConfig.addString( "applicationKey", appKey.toString() );
        siteConfig.addSet( "config" ).addBoolean( "some", false );

        final CreateContentParams createContentParams = CreateContentParams.create()
            .contentData( contentData )
            .displayName( "This is my page" )
            .parent( ContentPath.ROOT )
            .type( ContentTypeName.site() )
            .requireValid( true )
            .build();

        assertThrows( ContentDataValidationException.class, () -> this.contentService.create( createContentParams ) );
    }

    @Test
    void create_with_extra_data()
    {
        final PropertyTree siteData = new PropertyTree();

        final SiteConfig siteConfig = SiteConfig.create().application( ApplicationKey.from( "app" ) ).config( new PropertyTree() ).build();
        final PropertySet parentSet = siteData.getRoot();
        final PropertySet siteConfigAsSet = parentSet.addSet( "siteConfig" );
        siteConfigAsSet.addString( "applicationKey", siteConfig.getApplicationKey().toString() );
        siteConfigAsSet.addSet( "config", siteConfig.getConfig().getRoot().copy( parentSet.getTree() ) );

        SiteConfigsDataSerializer.toData( SiteConfigs.create().add( siteConfig ).build(), siteData.getRoot() );

        final CreateContentParams createContentParams = CreateContentParams.create()
            .contentData( siteData )
            .displayName( "This is my content" )
            .parent( ContentPath.ROOT )
            .type( ContentTypeName.site() )
            .build();

        final Content content = this.contentService.create( createContentParams );

        when( resourceService.processResource( isA( ResourceProcessor.class ) ) ).thenReturn( SiteDescriptor.create()
                                                                                                  .applicationKey(
                                                                                                      ApplicationKey.from( "app" ) )
                                                                                                  .xDataMappings( XDataMappings.from(
                                                                                                      XDataMapping.create()
                                                                                                          .xDataName( XDataName.from(
                                                                                                              "app:xdata1" ) )
                                                                                                          .allowContentTypes(
                                                                                                              "base:folder" )
                                                                                                          .optional( false )
                                                                                                          .build(), XDataMapping.create()
                                                                                                          .xDataName( XDataName.from(
                                                                                                              "app:xdata2" ) )
                                                                                                          .allowContentTypes(
                                                                                                              "base:folder" )
                                                                                                          .optional( true )
                                                                                                          .build(), XDataMapping.create()
                                                                                                          .xDataName( XDataName.from(
                                                                                                              "app:xdata3" ) )
                                                                                                          .allowContentTypes(
                                                                                                              "base:folder" )
                                                                                                          .optional( false )
                                                                                                          .build() ) )
                                                                                                  .build() );

        final XData xData1 = XData.create().name( XDataName.from( "app:xdata1" ) ).form( Form.create().build() ).build();
        when( xDataService.getByName( xData1.getName() ) ).thenReturn( xData1 );
        final XData xData2 = XData.create().name( XDataName.from( "app:xdata2" ) ).form( Form.create().build() ).build();
        when( xDataService.getByName( xData2.getName() ) ).thenReturn( xData2 );
        final XData xData3 = XData.create().name( XDataName.from( "app:xdata3" ) ).form( Form.create().build() ).build();
        when( xDataService.getByName( xData3.getName() ) ).thenReturn( xData3 );

        final Content storedContent = this.contentService.create( CreateContentParams.create()
                                                                      .contentData( new PropertyTree() )
                                                                      .displayName( "This is my content" )
                                                                      .parent( content.getPath() )
                                                                      .type( ContentTypeName.folder() )
                                                                      .extraDatas( ExtraDatas.create()
                                                                                       .add( new ExtraData( XDataName.from( "app:xdata1" ),
                                                                                                            new PropertyTree() ) )
                                                                                       .build() )
                                                                      .build() );

        assertEquals( 2, storedContent.getAllExtraData().getNames().getSet().size() );
        assertTrue( storedContent.getAllExtraData().getNames().contains( xData1.getName() ) );
        assertTrue( storedContent.getAllExtraData().getNames().contains( xData3.getName() ) );


    }

    @Test
    void create_with_missing_required_extra_data()
    {
        final PropertyTree siteData = new PropertyTree();

        final SiteConfig siteConfig = SiteConfig.create().application( ApplicationKey.from( "app" ) ).config( new PropertyTree() ).build();
        final PropertySet parentSet = siteData.getRoot();
        final PropertySet siteConfigAsSet = parentSet.addSet( "siteConfig" );
        siteConfigAsSet.addString( "applicationKey", siteConfig.getApplicationKey().toString() );
        siteConfigAsSet.addSet( "config", siteConfig.getConfig().getRoot().copy( parentSet.getTree() ) );

        SiteConfigsDataSerializer.toData( SiteConfigs.create().add( siteConfig ).build(), siteData.getRoot() );

        final CreateContentParams createContentParams = CreateContentParams.create()
            .contentData( siteData )
            .displayName( "This is my content" )
            .parent( ContentPath.ROOT )
            .type( ContentTypeName.site() )
            .build();

        final Content parent = this.contentService.create( createContentParams );

        final XDataName xdata = XDataName.from( "app:xdata1" );

        when( resourceService.processResource( isA( ResourceProcessor.class ) ) ).thenReturn( SiteDescriptor.create()
                                                                                                  .applicationKey(
                                                                                                      ApplicationKey.from( "app" ) )
                                                                                                  .xDataMappings( XDataMappings.from(
                                                                                                      XDataMapping.create()
                                                                                                          .xDataName( xdata )
                                                                                                          .allowContentTypes(
                                                                                                              "base:folder" )
                                                                                                          .optional( false )
                                                                                                          .build() ) )
                                                                                                  .build() );

        final XData xData1 = XData.create().name( xdata ).form( Form.create().build() ).build();
        when( xDataService.getByName( xData1.getName() ) ).thenReturn( xData1 );

        final Content content = this.contentService.create( CreateContentParams.create()
                                                                .contentData( new PropertyTree() )
                                                                .displayName( "This is my content" )
                                                                .parent( parent.getPath() )
                                                                .type( ContentTypeName.folder() )
                                                                .build() );

        assertEquals( 1, content.getAllExtraData().getNames().getSet().size() );
        assertEquals( new PropertyTree(), content.getAllExtraData().getMetadata( xdata ).getData() );
    }

    @Test
    void create_with_missing_required_x_data()
    {
        final PropertyTree siteData = new PropertyTree();

        final SiteConfig siteConfig = SiteConfig.create().application( ApplicationKey.from( "app" ) ).config( new PropertyTree() ).build();
        final PropertySet parentSet = siteData.getRoot();
        final PropertySet siteConfigAsSet = parentSet.addSet( "siteConfig" );
        siteConfigAsSet.addString( "applicationKey", siteConfig.getApplicationKey().toString() );
        siteConfigAsSet.addSet( "config", siteConfig.getConfig().getRoot().copy( parentSet.getTree() ) );

        SiteConfigsDataSerializer.toData( SiteConfigs.create().add( siteConfig ).build(), siteData.getRoot() );

        final CreateContentParams createContentParams = CreateContentParams.create()
            .contentData( siteData )
            .displayName( "This is my content" )
            .parent( ContentPath.ROOT )
            .type( ContentTypeName.site() )
            .build();

        final Content parent = this.contentService.create( createContentParams );

        final XDataName xdata = XDataName.from( "app:xdata1" );

        when( resourceService.processResource( isA( ResourceProcessor.class ) ) ).thenReturn( SiteDescriptor.create()
                                                                                                  .applicationKey(
                                                                                                      ApplicationKey.from( "app" ) )
                                                                                                  .xDataMappings( XDataMappings.from(
                                                                                                      XDataMapping.create()
                                                                                                          .xDataName( xdata )
                                                                                                          .allowContentTypes(
                                                                                                              "base:folder" )
                                                                                                          .optional( false )
                                                                                                          .build() ) )
                                                                                                  .build() );

        assertThrows( IllegalStateException.class, () -> this.contentService.create( CreateContentParams.create()
                                                                                         .contentData( new PropertyTree() )
                                                                                         .displayName( "This is my content" )
                                                                                         .parent( parent.getPath() )
                                                                                         .type( ContentTypeName.folder() )
                                                                                         .build() ) );

    }

    @Test
    void create_with_missing_optional_x_data()
    {
        final PropertyTree siteData = new PropertyTree();

        final SiteConfig siteConfig = SiteConfig.create().application( ApplicationKey.from( "app" ) ).config( new PropertyTree() ).build();
        final PropertySet parentSet = siteData.getRoot();
        final PropertySet siteConfigAsSet = parentSet.addSet( "siteConfig" );
        siteConfigAsSet.addString( "applicationKey", siteConfig.getApplicationKey().toString() );
        siteConfigAsSet.addSet( "config", siteConfig.getConfig().getRoot().copy( parentSet.getTree() ) );

        SiteConfigsDataSerializer.toData( SiteConfigs.create().add( siteConfig ).build(), siteData.getRoot() );

        final CreateContentParams createContentParams = CreateContentParams.create()
            .contentData( siteData )
            .displayName( "This is my content" )
            .parent( ContentPath.ROOT )
            .type( ContentTypeName.site() )
            .build();

        final Content parent = this.contentService.create( createContentParams );

        final XDataName xdata = XDataName.from( "app:xdata1" );

        when( resourceService.processResource( isA( ResourceProcessor.class ) ) ).thenReturn( SiteDescriptor.create()
                                                                                                  .applicationKey(
                                                                                                      ApplicationKey.from( "app" ) )
                                                                                                  .xDataMappings( XDataMappings.from(
                                                                                                      XDataMapping.create()
                                                                                                          .xDataName( xdata )
                                                                                                          .allowContentTypes(
                                                                                                              "base:folder" )
                                                                                                          .optional( true )
                                                                                                          .build() ) )
                                                                                                  .build() );

        final Content content = this.contentService.create( CreateContentParams.create()
                                                                .contentData( new PropertyTree() )
                                                                .displayName( "This is my content" )
                                                                .parent( parent.getPath() )
                                                                .type( ContentTypeName.folder() )
                                                                .build() );

        assertFalse( content.hasExtraData() );

    }

    @Test
    void create_with_not_supported_extra_data()
    {
        final CreateContentParams createContentParams = CreateContentParams.create()
            .contentData( new PropertyTree() )
            .displayName( "This is my page" )
            .parent( ContentPath.ROOT )
            .type( ContentTypeName.folder() )
            .extraDatas( ExtraDatas.create().add( new ExtraData( XDataName.from( "app:xdata" ), new PropertyTree() ) ).build() )
            .build();

        assertThrows( IllegalArgumentException.class, () -> this.contentService.create( createContentParams ) );
    }

    @Test
    void audit_data()
    {
        final ArgumentCaptor<LogAuditLogParams> captor = ArgumentCaptor.forClass( LogAuditLogParams.class );

        final CreateContentParams createContentParams = CreateContentParams.create()
            .contentData( new PropertyTree() )
            .displayName( "This is my content" )
            .parent( ContentPath.ROOT )
            .type( ContentTypeName.folder() )
            .build();

        Mockito.reset( auditLogService );

        final Content content = this.contentService.create( createContentParams );

        verify( auditLogService, atMostOnce() ).log( captor.capture() );

        final LogAuditLogParams log = captor.getValue();
        assertThat( log ).extracting( LogAuditLogParams::getType ).isEqualTo( "system.content.create" );
        assertThat( log ).extracting( l -> l.getData().getSet( "result" ) )
            .extracting( result -> result.getString( "id" ), result -> result.getString( "path" ) )
            .containsExactly( content.getId().toString(), content.getPath().toString() );
    }
}
