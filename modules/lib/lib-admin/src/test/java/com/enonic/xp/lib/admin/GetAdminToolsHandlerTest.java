package com.enonic.xp.lib.admin;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.admin.tool.AdminToolDescriptorService;
import com.enonic.xp.admin.tool.AdminToolDescriptors;
import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.lib.admin.mapper.AdminToolMapper;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetAdminToolsHandlerTest
{
    private GetAdminToolsHandler handler;

    private AdminToolDescriptorService adminToolDescriptorService;

    private ApplicationService applicationService;

    private LocaleService localeService;

    private Context context;

    @BeforeEach
    void setUp()
    {
        handler = new GetAdminToolsHandler();

        adminToolDescriptorService = mock( AdminToolDescriptorService.class );
        applicationService = mock( ApplicationService.class );
        localeService = mock( LocaleService.class );

        final BeanContext beanContext = mock( BeanContext.class );
        when( beanContext.getService( AdminToolDescriptorService.class ) ).thenReturn( () -> adminToolDescriptorService );
        when( beanContext.getService( ApplicationService.class ) ).thenReturn( () -> applicationService );
        when( beanContext.getService( LocaleService.class ) ).thenReturn( () -> localeService );

        final User user = User.create()
            .key( PrincipalKey.ofUser( IdProviderKey.system(), "testuser" ) )
            .login( "testuser" )
            .build();

        final AuthenticationInfo authInfo = AuthenticationInfo.create().user( user ).principals( RoleKeys.ADMIN ).build();

        context = ContextBuilder.create().authInfo( authInfo ).build();
        when( beanContext.getBinding( Context.class ) ).thenReturn( () -> context );

        handler.initialize( beanContext );
    }

    @Test
    void testExecute()
    {
        final ApplicationKey systemAppKey = ApplicationKey.from( "com.enonic.app.system" );
        final ApplicationKey userAppKey = ApplicationKey.from( "com.enonic.app.user" );

        final DescriptorKey systemToolKey = DescriptorKey.from( systemAppKey, "systemTool" );
        final DescriptorKey userToolKey = DescriptorKey.from( userAppKey, "userTool" );

        final AdminToolDescriptor systemTool = AdminToolDescriptor.create()
            .key( systemToolKey )
            .displayName( "System Tool" )
            .displayNameI18nKey( "tool.system.name" )
            .description( "System tool description" )
            .descriptionI18nKey( "tool.system.desc" )
            .addAllowedPrincipals( RoleKeys.ADMIN )
            .build();

        final AdminToolDescriptor userTool = AdminToolDescriptor.create()
            .key( userToolKey )
            .displayName( "User Tool" )
            .description( "User tool description" )
            .addAllowedPrincipals( RoleKeys.ADMIN )
            .build();

        final AdminToolDescriptors tools = AdminToolDescriptors.from( systemTool, userTool );

        when( adminToolDescriptorService.getAllowedAdminToolDescriptors( any( PrincipalKeys.class ) ) ).thenReturn( tools );

        final Application systemApp = mock( Application.class );
        when( systemApp.isSystem() ).thenReturn( true );
        when( applicationService.get( systemAppKey ) ).thenReturn( systemApp );

        final Application userApp = mock( Application.class );
        when( userApp.isSystem() ).thenReturn( false );
        when( applicationService.get( userAppKey ) ).thenReturn( userApp );

        when( adminToolDescriptorService.getIconByKey( systemToolKey ) ).thenReturn( "<svg>system</svg>" );
        when( adminToolDescriptorService.getIconByKey( userToolKey ) ).thenReturn( "<svg>user</svg>" );

        final MessageBundle bundle = mock( MessageBundle.class );
        when( bundle.localize( "tool.system.name" ) ).thenReturn( "Localized System Tool" );
        when( bundle.localize( "tool.system.desc" ) ).thenReturn( "Localized system description" );
        when( localeService.getBundle( any( ApplicationKey.class ), any( Locale.class ) ) ).thenReturn( bundle );
        when( localeService.getSupportedLocale( anyList(), any( ApplicationKey.class ) ) ).thenReturn( Locale.ENGLISH );

        final List<AdminToolMapper> result = context.callWith( handler::execute );

        assertNotNull( result );
        assertEquals( 2, result.size() );
    }

    @Test
    void testExecuteWithLocales()
    {
        final ApplicationKey appKey = ApplicationKey.from( "com.enonic.app.test" );
        final DescriptorKey toolKey = DescriptorKey.from( appKey, "testTool" );

        final AdminToolDescriptor tool = AdminToolDescriptor.create()
            .key( toolKey )
            .displayName( "Test Tool" )
            .displayNameI18nKey( "tool.test.name" )
            .description( "Test description" )
            .descriptionI18nKey( "tool.test.desc" )
            .addAllowedPrincipals( RoleKeys.ADMIN )
            .build();

        final AdminToolDescriptors tools = AdminToolDescriptors.from( tool );

        when( adminToolDescriptorService.getAllowedAdminToolDescriptors( any( PrincipalKeys.class ) ) ).thenReturn( tools );

        final Application app = mock( Application.class );
        when( app.isSystem() ).thenReturn( false );
        when( applicationService.get( appKey ) ).thenReturn( app );

        when( adminToolDescriptorService.getIconByKey( toolKey ) ).thenReturn( null );

        final MessageBundle bundle = mock( MessageBundle.class );
        when( bundle.localize( "tool.test.name" ) ).thenReturn( "Localized Test Tool" );
        when( bundle.localize( "tool.test.desc" ) ).thenReturn( "Localized test description" );
        when( localeService.getBundle( any( ApplicationKey.class ), any( Locale.class ) ) ).thenReturn( bundle );
        when( localeService.getSupportedLocale( anyList(), any( ApplicationKey.class ) ) ).thenReturn( Locale.ENGLISH );

        handler.setLocales( List.of( "en", "no" ) );

        final List<AdminToolMapper> result = context.callWith( handler::execute );

        assertNotNull( result );
        assertEquals( 1, result.size() );
    }

    @Test
    void testExecuteWithNoI18nKeys()
    {
        final ApplicationKey appKey = ApplicationKey.from( "com.enonic.app.simple" );
        final DescriptorKey toolKey = DescriptorKey.from( appKey, "simpleTool" );

        final AdminToolDescriptor tool = AdminToolDescriptor.create()
            .key( toolKey )
            .displayName( "Simple Tool" )
            .description( "Simple description" )
            .addAllowedPrincipals( RoleKeys.ADMIN )
            .build();

        final AdminToolDescriptors tools = AdminToolDescriptors.from( tool );

        when( adminToolDescriptorService.getAllowedAdminToolDescriptors( any( PrincipalKeys.class ) ) ).thenReturn( tools );

        final Application app = mock( Application.class );
        when( app.isSystem() ).thenReturn( true );
        when( applicationService.get( appKey ) ).thenReturn( app );

        when( adminToolDescriptorService.getIconByKey( toolKey ) ).thenReturn( "<svg>icon</svg>" );

        final List<AdminToolMapper> result = context.callWith( handler::execute );

        assertNotNull( result );
        assertEquals( 1, result.size() );
    }
}
