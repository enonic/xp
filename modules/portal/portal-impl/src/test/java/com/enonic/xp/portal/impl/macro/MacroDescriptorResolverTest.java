package com.enonic.xp.portal.impl.macro;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroDescriptorService;
import com.enonic.xp.macro.MacroDescriptors;
import com.enonic.xp.macro.MacroKey;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MacroDescriptorResolverTest
{
    private static final ApplicationKeys APPLICATIONS = ApplicationKeys.from( "app1", "app2" );

    private MacroDescriptorService macroDescriptorService;

    private MacroDescriptorResolver resolver;

    @BeforeEach
    void setUp()
    {
        macroDescriptorService = mock( MacroDescriptorService.class );
        when( macroDescriptorService.getByApplication( any() ) ).thenReturn( MacroDescriptors.empty() );
        resolver = new MacroDescriptorResolver( macroDescriptorService );
    }

    @Test
    void firstApplicationProvidingTheMacro()
    {
        final MacroDescriptor app2Macro = descriptor( "app2:mymacro" );
        final MacroDescriptor systemMacro = descriptor( "system:mymacro" );

        assertSame( app2Macro, resolver.resolve( APPLICATIONS, "mymacro" ) );
        assertSame( systemMacro, resolver.resolve( ApplicationKeys.empty(), "mymacro" ) );
    }

    @Test
    void exactNameBeforeIgnoringCase()
    {
        final MacroDescriptor app1Macro = descriptor( "app1:MyMacro" );
        when( macroDescriptorService.getByApplication( ApplicationKey.from( "app1" ) ) ).thenReturn( MacroDescriptors.from( app1Macro ) );
        final MacroDescriptor app2Macro = descriptor( "app2:mymacro" );

        assertSame( app2Macro, resolver.resolve( APPLICATIONS, "mymacro" ) );
        assertSame( app1Macro, resolver.resolve( APPLICATIONS, "MYMACRO" ) );
    }

    @Test
    void applicationsBeforeSystem()
    {
        final MacroDescriptor app1Macro = descriptor( "app1:MyMacro" );
        when( macroDescriptorService.getByApplication( ApplicationKey.from( "app1" ) ) ).thenReturn( MacroDescriptors.from( app1Macro ) );
        descriptor( "system:mymacro" );

        assertSame( app1Macro, resolver.resolve( APPLICATIONS, "mymacro" ) );
    }

    @Test
    void unknownMacro()
    {
        assertNull( resolver.resolve( APPLICATIONS, "mymacro" ) );
    }

    private MacroDescriptor descriptor( final String key )
    {
        final MacroKey macroKey = MacroKey.from( key );
        final MacroDescriptor descriptor = MacroDescriptor.create().key( macroKey ).build();
        when( macroDescriptorService.getByKey( macroKey ) ).thenReturn( descriptor );
        return descriptor;
    }
}
