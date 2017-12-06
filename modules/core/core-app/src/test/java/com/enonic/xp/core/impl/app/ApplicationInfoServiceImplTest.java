package com.enonic.xp.core.impl.app;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationInfo;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.auth.AuthDescriptor;
import com.enonic.xp.auth.AuthDescriptorMode;
import com.enonic.xp.auth.AuthDescriptorService;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Contents;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.descriptor.Descriptors;
import com.enonic.xp.form.Form;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroDescriptorService;
import com.enonic.xp.macro.MacroDescriptors;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.page.PageDescriptors;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.LayoutDescriptors;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.region.PartDescriptors;
import com.enonic.xp.region.RegionDescriptor;
import com.enonic.xp.region.RegionDescriptors;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.ContentTypes;
import com.enonic.xp.schema.relationship.RelationshipType;
import com.enonic.xp.schema.relationship.RelationshipTypeService;
import com.enonic.xp.schema.relationship.RelationshipTypes;
import com.enonic.xp.security.AuthConfig;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.UserStore;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.UserStores;
import com.enonic.xp.task.TaskDescriptor;
import com.enonic.xp.task.TaskDescriptorService;

import static org.junit.Assert.*;

public class ApplicationInfoServiceImplTest
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

    private AuthDescriptorService authDescriptorService;

    private ApplicationInfoServiceImpl service;

    private ApplicationKey applicationKey;

    @Before
    public void initService()
    {
        this.service = new ApplicationInfoServiceImpl();

        this.contentTypeService = Mockito.mock( ContentTypeService.class );
        this.pageDescriptorService = Mockito.mock( PageDescriptorService.class );
        this.partDescriptorService = Mockito.mock( PartDescriptorService.class );
        this.layoutDescriptorService = Mockito.mock( LayoutDescriptorService.class );
        this.relationshipTypeService = Mockito.mock( RelationshipTypeService.class );
        this.macroDescriptorService = Mockito.mock( MacroDescriptorService.class );
        this.contentService = Mockito.mock( ContentService.class );
        this.resourceService = Mockito.mock( ResourceService.class );
        this.taskDescriptorService = Mockito.mock( TaskDescriptorService.class );
        this.securityService = Mockito.mock( SecurityService.class );
        this.authDescriptorService = Mockito.mock( AuthDescriptorService.class );

        this.service.setContentTypeService( this.contentTypeService );
        this.service.setPageDescriptorService( this.pageDescriptorService );
        this.service.setPartDescriptorService( this.partDescriptorService );
        this.service.setLayoutDescriptorService( this.layoutDescriptorService );
        this.service.setRelationshipTypeService( this.relationshipTypeService );
        this.service.setMacroDescriptorService( this.macroDescriptorService );
        this.service.setContentService( this.contentService );
        this.service.setResourceService( this.resourceService );
        this.service.setTaskDescriptorService( this.taskDescriptorService );
        this.service.setSecurityService( this.securityService );
        this.service.setAuthDescriptorService( this.authDescriptorService );

    }

    @Before
    public void initKey()
    {
        this.applicationKey = ApplicationKey.from( "testapplication" );
    }

    @Test
    public void testContentTypes()
    {
        mockContentTypes( this.applicationKey );
        final ContentTypes contentTypes = this.service.getContentTypes( this.applicationKey );

        assertEquals( contentTypes.getSize(), 1 );
    }

    @Test
    public void testPages()
    {
        mockPageDescriptors( this.applicationKey );
        final PageDescriptors pageDescriptors = this.service.getPageDescriptors( this.applicationKey );

        assertEquals( pageDescriptors.getSize(), 2 );
    }

    @Test
    public void testParts()
    {
        mockPartDescriptors( this.applicationKey );
        final PartDescriptors partDescriptors = this.service.getPartDescriptors( this.applicationKey );

        assertEquals( partDescriptors.getSize(), 2 );
    }

    @Test
    public void testLayouts()
    {
        mockLayoutDescriptors( this.applicationKey );
        final LayoutDescriptors layoutDescriptors = this.service.getLayoutDescriptors( this.applicationKey );

        assertEquals( layoutDescriptors.getSize(), 2 );
    }

    @Test
    public void testRelationshipTypes()
    {
        mockRelationshipTypes( this.applicationKey );
        final RelationshipTypes relationshipTypes = this.service.getRelationshipTypes( this.applicationKey );

        assertEquals( relationshipTypes.getSize(), 2 );
    }

    @Test
    public void testReferences()
    {
        mockReferences( this.applicationKey );
        final Contents contentReferences = this.service.getContentReferences( this.applicationKey );

        assertEquals( contentReferences.getSize(), 2 );
    }

    @Test
    public void testTasks()
    {
        mockTasks( this.applicationKey );
        final Descriptors<TaskDescriptor> tasks = this.service.getTaskDescriptors( this.applicationKey );

        assertEquals( tasks.getSize(), 2 );
    }

    @Test
    public void testMacros()
    {
        mockMacros( this.applicationKey );
        final MacroDescriptors macros = this.service.getMacroDescriptors( this.applicationKey );

        assertEquals( macros.getSize(), 2 );
    }

    @Test
    public void testIdProvider()
    {
        mockIdProvider( this.applicationKey );
        final AuthDescriptor authDescriptor = this.service.getAuthDescriptor( this.applicationKey );

        assertNotNull( authDescriptor );

        final UserStores userStores = this.service.getUserStoreReferences( this.applicationKey );

        assertEquals( userStores.getSize(), 2 );
    }

    @Test
    public void testApplicationInfo()
    {
        mockApplicationInfo( applicationKey );
        final ApplicationInfo applicationInfo = this.service.getApplicationInfo( applicationKey );

        assertEquals( applicationInfo.getContentTypes().getSize(), 1 );
        assertEquals( applicationInfo.getPages().getSize(), 2 );
        assertEquals( applicationInfo.getParts().getSize(), 2 );
        assertEquals( applicationInfo.getLayouts().getSize(), 2 );
        assertEquals( applicationInfo.getRelations().getSize(), 2 );
        assertEquals( applicationInfo.getContentReferences().getSize(), 2 );
        assertEquals( applicationInfo.getTasks().getSize(), 2 );
        assertEquals( applicationInfo.getMacros().getSize(), 2 );
        assertEquals( applicationInfo.getUserStoreReferences().getSize(), 2 );
    }

    private void mockContentTypes( final ApplicationKey applicationKey )
    {
        final ContentType contentType =
            ContentType.create().name( ContentTypeName.media() ).form( Form.create().build() ).setAbstract().setFinal().allowChildContent(
                true ).setBuiltIn().contentDisplayNameScript( "contentDisplayNameScript" ).metadata( null ).displayName(
                "displayName" ).description( "description" ).modifiedTime( Instant.ofEpochSecond( 1000 ) ).createdTime(
                Instant.ofEpochSecond( 1000 ) ).creator( PrincipalKey.ofAnonymous() ).modifier( PrincipalKey.ofAnonymous() ).build();

        final ContentTypes contentTypes = ContentTypes.from( contentType );
        Mockito.when( this.contentTypeService.getByApplication( applicationKey ) ).thenReturn( contentTypes );
    }

    private void mockPageDescriptors( final ApplicationKey applicationKey )
    {
        final PageDescriptor pageDescriptor1 = PageDescriptor.create().
            displayName( "Landing page" ).
            config( Form.create().build() ).
            regions( RegionDescriptors.create().build() ).
            key( DescriptorKey.from( "module:landing-page" ) ).
            build();

        final PageDescriptor pageDescriptor2 = PageDescriptor.create().
            displayName( "Log out" ).
            config( Form.create().build() ).
            regions( RegionDescriptors.create().build() ).
            key( DescriptorKey.from( "module:logout-page" ) ).
            build();

        final PageDescriptors pageDescriptors = PageDescriptors.from( pageDescriptor1, pageDescriptor2 );
        Mockito.when( pageDescriptorService.getByApplication( applicationKey ) ).thenReturn( pageDescriptors );
    }

    private void mockPartDescriptors( final ApplicationKey applicationKey )
    {
        final PartDescriptor partDescriptor1 = PartDescriptor.create().
            displayName( "News part" ).
            config( Form.create().build() ).
            key( DescriptorKey.from( "module:new-part" ) ).
            build();

        final PartDescriptor partDescriptor2 = PartDescriptor.create().
            displayName( "News part2" ).
            config( Form.create().build() ).
            key( DescriptorKey.from( "module:new-part2" ) ).
            build();

        Mockito.when( partDescriptorService.getByApplication( applicationKey ) ).
            thenReturn( PartDescriptors.from( partDescriptor1, partDescriptor2 ) );
    }

    private void mockLayoutDescriptors( final ApplicationKey applicationKey )
    {
        final LayoutDescriptor layoutDescriptor1 = LayoutDescriptor.create().
            displayName( "Fancy layout" ).
            config( Form.create().build() ).
            regions( RegionDescriptors.create().
                add( RegionDescriptor.create().name( "left" ).build() ).
                add( RegionDescriptor.create().name( "right" ).build() ).
                build() ).
            key( DescriptorKey.from( "application:fancy-layout" ) ).
            build();

        final LayoutDescriptor layoutDescriptor2 = LayoutDescriptor.create().
            displayName( "Putty layout" ).
            config( Form.create().build() ).
            regions( RegionDescriptors.create().
                add( RegionDescriptor.create().name( "top" ).build() ).
                add( RegionDescriptor.create().name( "bottom" ).build() ).
                build() ).
            key( DescriptorKey.from( "application:putty-layout" ) ).
            build();

        Mockito.when( layoutDescriptorService.getByApplication( applicationKey ) ).
            thenReturn( LayoutDescriptors.from( layoutDescriptor1, layoutDescriptor2 ) );

    }

    private void mockRelationshipTypes( final ApplicationKey applicationKey )
    {
        final RelationshipTypes relationshipTypes =
            RelationshipTypes.from( RelationshipType.create().name( "myapplication:person" ).build(),
                                    RelationshipType.create().name( "myapplication:site" ).build() );

        Mockito.when( this.relationshipTypeService.getByApplication( applicationKey ) ).thenReturn( relationshipTypes );
    }

    private void mockMacros( final ApplicationKey applicationKey )
    {
        final MacroDescriptor macroDescriptor1 = MacroDescriptor.create().
            key( MacroKey.from( "my-app1:macro1" ) ).
            description( "my description" ).
            displayName( "A macro" ).
            form( Form.create().build() ).
            build();

        final MacroDescriptor macroDescriptor2 = MacroDescriptor.create().
            key( MacroKey.from( "my-app2:macro2" ) ).
            description( "my description" ).
            displayName( "B macro" ).
            form( Form.create().build() ).
            build();

        final MacroDescriptors macroDescriptors = MacroDescriptors.from( macroDescriptor1, macroDescriptor2 );

        Mockito.when(
            this.macroDescriptorService.getByApplications( ApplicationKeys.from( applicationKey, ApplicationKey.SYSTEM ) ) ).thenReturn(
            macroDescriptors );
    }

    private void mockReferences( final ApplicationKey applicationKey )
    {
        final Content content1 = Content.create().
            id( ContentId.from( "id1" ) ).
            name( "name1" ).
            displayName( "My Content 1" ).
            parentPath( ContentPath.from( "/a/b" ) ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            modifiedTime( Instant.ofEpochSecond( 0 ) ).
            creator( PrincipalKey.from( "user:system:admin" ) ).
            createdTime( Instant.ofEpochSecond( 0 ) ).
            build();

        final Content content2 = Content.create().
            id( ContentId.from( "id2" ) ).
            name( "name2" ).
            displayName( "My Content 2" ).
            parentPath( ContentPath.from( "/a/c" ) ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            modifiedTime( Instant.ofEpochSecond( 0 ) ).
            creator( PrincipalKey.from( "user:system:admin" ) ).
            createdTime( Instant.ofEpochSecond( 0 ) ).
            build();

        final Contents contents = Contents.from( content1, content2 );

        Mockito.when( this.contentService.findByApplicationKey( applicationKey ) ).thenReturn( contents );
    }

    private void mockTasks( final ApplicationKey applicationKey )
    {
        final TaskDescriptor taskDescriptor1 = TaskDescriptor.create().
            key( DescriptorKey.from( ApplicationKey.SYSTEM, "task1" ) ).
            description( "description1" ).
            config( Form.create().build() ).
            build();

        final TaskDescriptor taskDescriptor2 = TaskDescriptor.create().
            key( DescriptorKey.from( ApplicationKey.SYSTEM, "task2" ) ).
            description( "description2" ).
            config( Form.create().build() ).
            build();

        final Descriptors<TaskDescriptor> descriptors = Descriptors.from( taskDescriptor1, taskDescriptor2 );

        Mockito.when( this.taskDescriptorService.getTasks( applicationKey ) ).thenReturn( descriptors );
    }

    private void mockIdProvider( final ApplicationKey applicationKey )
    {
        final AuthDescriptor authDescriptor = AuthDescriptor.create().
            config( Form.create().build() ).
            key( applicationKey ).
            mode( AuthDescriptorMode.EXTERNAL ).
            build();

        final UserStore userStore1 = UserStore.create().
            displayName( "userStore1" ).
            key( UserStoreKey.from( "userStore1" ) ).
            authConfig( AuthConfig.
                create().
                applicationKey( applicationKey ).
                config( new PropertyTree() ).
                build() ).
            build();

        final UserStore userStore2 = UserStore.create().
            displayName( "userStore2" ).
            key( UserStoreKey.from( "userStore2" + "" ) ).
            authConfig( AuthConfig.
                create().
                applicationKey( applicationKey ).
                config( new PropertyTree() ).
                build() ).
            build();

        Mockito.when( this.authDescriptorService.getDescriptor( applicationKey ) ).thenReturn( authDescriptor );
        Mockito.when( this.securityService.getUserStores() ).thenReturn( UserStores.from( userStore1, userStore2 ) );

    }

    private void mockApplicationInfo( final ApplicationKey applicationKey )
    {
        mockContentTypes( applicationKey );
        mockPageDescriptors( applicationKey );
        mockPartDescriptors( applicationKey );
        mockLayoutDescriptors( applicationKey );
        mockRelationshipTypes( applicationKey );
        mockMacros( applicationKey );
        mockReferences( applicationKey );
        mockTasks( applicationKey );
        mockIdProvider( applicationKey );
    }
/*
    private void mockDeployment( final ApplicationKey applicationKey )
    {
        final Resource resourceMock = Mockito.mock( Resource.class );
        Mockito.when( resourceMock.exists() ).thenReturn( true );

        Mockito.when( this.resourceService.getResource( ResourceKey.from( applicationKey, "/main.js" ) ) ).thenReturn( resourceMock );
    }*/
}
