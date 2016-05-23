package com.enonic.xp.portal.impl.macro;

import org.apache.commons.lang.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.macro.MacroProcessor;
import com.enonic.xp.portal.macro.MacroProcessorScriptFactory;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptExports;

@Component
public final class MacroProcessorScriptFactoryImpl
    implements MacroProcessorScriptFactory
{
    private PortalScriptService scriptService;

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
            // TODO make resolving dynamic
            switch ( name )
            {
                case "youtube":
                    return new YoutubeMacroProcessor();
                case "tweet":
                    return new TwitterMacroProcessor();
                case "code":
                    return new EmbeddedCodeMacroProcessor();
                case "noformat":
                    return new NoFormatMacroProcessor();
                default:
                    return null;
            }
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

}
