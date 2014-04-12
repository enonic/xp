package com.enonic.wem.core.module;

import java.io.IOException;
import java.nio.file.Path;

import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.core.support.export.AbstractEntityExporter;
import com.enonic.wem.core.support.export.XMLFilename;

@XMLFilename("module.xml")
public class ModuleExporter
    extends AbstractEntityExporter<Module, Module.Builder>
{
    private final ModuleXmlSerializer xmlSerializer = new ModuleXmlSerializer();

    @Override
    protected String toXMLString( final Module module )
    {
        return xmlSerializer.toString( module );
    }

    @Override
    protected Module.Builder fromXMLString( final String xml, final Path directoryPath )
        throws IOException
    {
        final ModuleKey moduleKey = ModuleKey.from( resolveId( directoryPath ) );
        final Module.Builder moduleBuilder = Module.newModule().moduleKey( moduleKey );

        xmlSerializer.toModule( xml, moduleBuilder );

        return moduleBuilder;
    }
}
