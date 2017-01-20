package com.enonic.xp.portal.impl.macro;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.google.common.collect.Maps;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.macro.MacroProcessor;
import com.enonic.xp.portal.macro.MacroProcessorFactory;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptExports;

@Component
public final class MacroProcessorFactoryImpl
    implements MacroProcessorFactory
{
    private PortalScriptService scriptService;

    private final Map<String, MacroProcessor> macroProcessors;

    public MacroProcessorFactoryImpl()
    {
        this.macroProcessors = Maps.newConcurrentMap();
    }

    @Override
    public MacroProcessor fromDir( final ResourceKey dir )
    {
        return fromScript( dir.resolve( dir.getName() + ".js" ) );
    }

    @Override
    public MacroProcessor fromScript( final ResourceKey scriptResourceKey )
    {
        if ( isSystem( scriptResourceKey ) )
        {
            final String name = StringUtils.substringBefore( scriptResourceKey.getName(), ".js" );
            return this.macroProcessors.get( name );
        }
        else
        {
            final ScriptExports exports = this.scriptService.execute( scriptResourceKey );
            return new MacroProcessorScript( exports );
        }
    }

    private boolean isSystem( ResourceKey scriptResourceKey )
    {
        return ApplicationKey.SYSTEM.equals( scriptResourceKey.getApplicationKey() );
    }

    @Reference
    public void setScriptService( final PortalScriptService scriptService )
    {
        this.scriptService = scriptService;
    }


    @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.MULTIPLE)
    public void addMacroProcessor( final BuiltInMacroProcessor value )
    {
        this.macroProcessors.put( value.getName(), value );
    }

    public void removeMacroProcessor( final BuiltInMacroProcessor value )
    {
        this.macroProcessors.remove( value.getName() );
    }
}
