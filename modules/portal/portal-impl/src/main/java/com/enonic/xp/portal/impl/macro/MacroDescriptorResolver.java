package com.enonic.xp.portal.impl.macro;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroDescriptorService;
import com.enonic.xp.macro.MacroKey;

/**
 * Resolves the descriptor of a macro named in rich text, among the applications of the site or project the text
 * belongs to: the macro of that name in the first application providing one, then the same ignoring case, then the
 * built-in macro of that name.
 */
public final class MacroDescriptorResolver
{
    private final MacroDescriptorService macroDescriptorService;

    public MacroDescriptorResolver( final MacroDescriptorService macroDescriptorService )
    {
        this.macroDescriptorService = macroDescriptorService;
    }

    /**
     * @param applications the applications of the site or project, in their order
     * @param macroName    name of the macro, as written in the text
     * @return the descriptor, or {@code null} when no application provides the macro
     */
    public MacroDescriptor resolve( final ApplicationKeys applications, final String macroName )
    {
        for ( final ApplicationKey application : applications )
        {
            final MacroDescriptor descriptor = macroDescriptorService.getByKey( MacroKey.from( application, macroName ) );
            if ( descriptor != null )
            {
                return descriptor;
            }
        }

        for ( final ApplicationKey application : applications )
        {
            final MacroDescriptor descriptor = macroDescriptorService.getByApplication( application )
                .stream()
                .filter( candidate -> candidate.getName().equalsIgnoreCase( macroName ) )
                .findFirst()
                .orElse( null );
            if ( descriptor != null )
            {
                return descriptor;
            }
        }

        return macroDescriptorService.getByKey( MacroKey.from( ApplicationKey.SYSTEM, macroName ) );
    }
}
