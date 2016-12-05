package com.enonic.xp.core.impl.content.processor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.Principals;
import com.enonic.xp.security.Role;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;

import static org.junit.Assert.*;

public class RoleContentProcessorTest
{

    private final RoleContentProcessor roleContentProcessor = new RoleContentProcessor();

    private SecurityService securityService;

    @Before
    public void setUp()
        throws Exception
    {
        this.securityService = Mockito.mock( SecurityService.class );
        roleContentProcessor.setSecurityService( this.securityService );
    }

    @Test
    public void testSupports()
    {
        ContentType contentType = ContentType.create().superType( ContentTypeName.structured() ).name( ContentTypeName.site() ).build();
        assertTrue( roleContentProcessor.supports( contentType ) );

        contentType = ContentType.create().superType( ContentTypeName.structured() ).name( ContentTypeName.media() ).build();
        assertFalse( roleContentProcessor.supports( contentType ) );
    }

    @Test
    public void testProcessUpdateByAdmin()
    {
        ProcessUpdateParams params = createProcessUpdateParams( PrincipalKeys.from( RoleKeys.ADMIN ), "white", "blue" );

        final ProcessUpdateResult result = this.roleContentProcessor.processUpdate( params );

        assertNull( result );
    }

    @Test
    public void testProcessUpdateByContentAdmin()
    {
        ProcessUpdateParams params = createProcessUpdateParams( PrincipalKeys.from( RoleKeys.CONTENT_MANAGER_ADMIN ), "white", "blue" );

        final ProcessUpdateResult result = this.roleContentProcessor.processUpdate( params );

        assertNull( result );
    }

    @Test
    public void testProcessUpdateWithNoChanges()
    {
        ProcessUpdateParams params = createProcessUpdateParams( PrincipalKeys.from( RoleKeys.ADMIN ), "white", "white" );

        final ProcessUpdateResult result = this.roleContentProcessor.processUpdate( params );

        assertNull( result );
    }

    @Test(expected = RoleRequiredException.class)
    public void testProcessUpdateWithNoRights()
        throws RoleRequiredException
    {
        ProcessUpdateParams params = createProcessUpdateParams( PrincipalKeys.empty(), "white", "blue" );

        this.roleContentProcessor.processUpdate( params );
    }

    @Test
    public void testProcessUpdateWithNoRightsAndNoChanges()
    {
        ProcessUpdateParams params = createProcessUpdateParams( PrincipalKeys.empty(), "white", "white" );

        final ProcessUpdateResult result = this.roleContentProcessor.processUpdate( params );

        assertNull( result );
    }

    private ProcessUpdateParams createProcessUpdateParams( PrincipalKeys roleKeys, String oldValue, String newValue )
    {
        User modifier = this.createUser();

        final ImmutableList.Builder<Role> rolesBuilder = ImmutableList.builder();
        roleKeys.forEach( roleKey -> rolesBuilder.add( Role.create().key( roleKey ).displayName( roleKey.toString() ).build() ) );
        Principals roles = Principals.from( rolesBuilder.build() );

        Content originalContent = this.createContent( oldValue );
        Content editedContent = this.createContent( newValue );

        Mockito.when( this.securityService.getMemberships( Mockito.eq( modifier.getKey() ) ) ).thenReturn( roleKeys );
        Mockito.when( this.securityService.getPrincipals( Mockito.eq( roleKeys ) ) ).thenReturn( roles );

        return ProcessUpdateParams.create().
            originalContent( originalContent ).
            editedContent( editedContent ).
            modifier( modifier ).
            contentType( this.createSiteContentType() ).
            build();
    }

    private ContentType createSiteContentType()
    {
        return ContentType.create().name( ContentTypeName.site() ).superType( ContentTypeName.structured() ).build();
    }

    private PropertySet createSiteConfig( final String bgColor )
    {
        PropertySet set = new PropertySet();
        set.addString( "applicationKey", "com.enonic.app.test" );
        PropertySet siteConfig = set.addSet( "config" );
        siteConfig.addString( "backgroundColor", bgColor );
        return set;
    }

    private Content createContent( final String siteConfigBgColor )
    {
        PropertyTree data = new PropertyTree();
        data.setSet( "siteConfig", this.createSiteConfig( siteConfigBgColor ) );
        return Content.create( ContentTypeName.site() ).name( "Site content" ).parentPath( ContentPath.ROOT ).data( data ).id(
            ContentId.from( "content-id" ) ).build();
    }

    private User createUser()
    {
        return User.create().email( "user@email.com" ).login( "userlogin" ).build();
    }
}
