package com.enonic.wem.core.module;

import java.io.IOException;
import java.nio.file.Path;

import javax.inject.Inject;

import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.core.support.export.AbstractEntityExporter;
import com.enonic.wem.core.support.export.XMLFilename;

@XMLFilename("module.xml")
public class ModuleExporter
    extends AbstractEntityExporter<Module, ModuleBuilder>
{
    private final ModuleXmlSerializer xmlSerializer = new ModuleXmlSerializer();

    @Inject
    protected ModuleResourcePathResolver pathResolver;

    @Override
    protected String toXMLString( final Module module )
    {
        return xmlSerializer.toString( module );
    }

    @Override
    protected ModuleBuilder fromXMLString( final String xml, final Path directoryPath )
        throws IOException
    {
        final ModuleKey moduleKey = ModuleKey.from( resolveId( directoryPath ) );
        final ModuleBuilder moduleBuilder = new ModuleBuilder().moduleKey( moduleKey );

        xmlSerializer.toModule( xml, moduleBuilder );
        moduleBuilder.moduleDir( this.pathResolver.resolveModulePath( moduleKey ).toFile() );

        return moduleBuilder;
    }
}
