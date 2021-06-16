package com.enonic.xp.admin.impl.rest.resource.macro.json;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.admin.impl.rest.resource.macro.MacroIconUrlResolver;
import com.enonic.xp.admin.impl.rest.resource.schema.content.LocaleMessageResolver;
import com.enonic.xp.admin.impl.rest.resource.schema.mixin.InlineMixinResolver;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroDescriptors;

public class MacrosJson
{

    private final List<MacroDescriptorJson> macros;

    public MacrosJson( final Builder builder )
    {
        List<MacroDescriptorJson> notSortedMacros = new ArrayList<>();
        if ( builder.macroDescriptors != null )
        {
            for ( final MacroDescriptor macroDescriptor : builder.macroDescriptors )
            {
                notSortedMacros.add( MacroDescriptorJson.create().
                    setMacroDescriptor( macroDescriptor ).
                    setMacroIconUrlResolver( builder.macroIconUrlResolver ).
                    setLocaleMessageResolver( builder.localeMessageResolver ).
                    setInlineMixinResolver( builder.inlineMixinResolver ).
                    build() );
            }
        }
        macros = getSortedMacros( notSortedMacros );
    }

    public MacrosJson( final List<MacroDescriptorJson> macroDescriptors )
    {
        macros = getSortedMacros( macroDescriptors );
    }

    private List<MacroDescriptorJson> getSortedMacros( List<MacroDescriptorJson> notSortedMacros )
    {
        return notSortedMacros.stream().sorted( Comparator.comparing( MacroDescriptorJson::getDisplayName ) ).collect(
            Collectors.toList() );
    }

    @SuppressWarnings("unused")
    public List<MacroDescriptorJson> getMacros()
    {
        return macros;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private MacroDescriptors macroDescriptors;

        private MacroIconUrlResolver macroIconUrlResolver;

        private LocaleMessageResolver localeMessageResolver;

        private InlineMixinResolver inlineMixinResolver;

        public Builder setMacroDescriptors( final MacroDescriptors macroDescriptors )
        {
            this.macroDescriptors = macroDescriptors;
            return this;
        }

        public Builder setMacroIconUrlResolver( final MacroIconUrlResolver macroIconUrlResolver )
        {
            this.macroIconUrlResolver = macroIconUrlResolver;
            return this;
        }

        public Builder setLocaleMessageResolver( final LocaleMessageResolver localeMessageResolver )
        {
            this.localeMessageResolver = localeMessageResolver;
            return this;
        }

        public Builder setInlineMixinResolver( final InlineMixinResolver inlineMixinResolver )
        {
            this.inlineMixinResolver = inlineMixinResolver;
            return this;
        }

        public MacrosJson build()
        {
            return new MacrosJson( this );
        }
    }
}
