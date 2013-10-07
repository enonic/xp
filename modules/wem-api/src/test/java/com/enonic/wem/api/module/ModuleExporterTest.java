package com.enonic.wem.api.module;

import java.nio.file.Paths;

import org.junit.Ignore;
import org.junit.Test;

import com.google.common.io.ByteStreams;

import com.enonic.wem.api.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.schema.content.QualifiedContentTypeNames;
import com.enonic.wem.api.schema.content.form.Form;
import com.enonic.wem.api.schema.content.form.Input;
import com.enonic.wem.api.schema.content.form.inputtype.InputTypes;

import static com.enonic.wem.api.module.ModuleFileEntry.directoryBuilder;
import static com.enonic.wem.api.module.ModuleFileEntry.newFileEntry;

public class ModuleExporterTest
{
    @Test
    @Ignore
    public void testExportModuleToZip()
        throws Exception
    {
        final ModuleFileEntry publicDir = directoryBuilder( "public" ).
            addEntry( newFileEntry( "file1.txt", ByteStreams.asByteSource( "some data".getBytes() ) ) ).
            build();
        final ModuleFileEntry templatesDir = directoryBuilder( "templates" ).
            addEntry( newFileEntry( "template1.txt", ByteStreams.asByteSource( "some more data".getBytes() ) ) ).
            build();

        final Form config = Form.newForm().
            addFormItem( Input.newInput().name( "some-name" ).inputType( InputTypes.TEXT_LINE ).build() ).
            build();

        final QualifiedContentTypeNames requiredCtypes = QualifiedContentTypeNames.from( "ctype1", "ctype2", "ctype3" );
        final ModuleKeys requiredModules = ModuleKeys.from( ModuleKey.from( "modA-1.0.0" ), ModuleKey.from( "modB-1.0.0" ) );

        final Module module = Module.newModule().
            moduleKey( ModuleKey.from( "testmodule-1.0.0" ) ).
            displayName( "module display name" ).
            info( "module-info" ).
            url( "http://enonic.net" ).
            vendorName( "Enonic" ).
            vendorUrl( "https://www.enonic.com" ).
            minSystemVersion( ModuleVersion.from( 5, 0, 0 ) ).
            maxSystemVersion( ModuleVersion.from( 6, 0, 0 ) ).
            addModuleDependency( ModuleKey.from( "modulefoo-1.0.0" ) ).
            addContentTypeDependency( QualifiedContentTypeName.from( "article" ) ).
            addModuleDependencies( requiredModules ).
            addContentTypeDependencies( requiredCtypes ).
            config( config ).
            addFileEntry( publicDir ).
            addFileEntry( templatesDir ).
            addFileEntry( directoryBuilder( "emptydir" ).build() ).
            build();

        final String exportFile = "~/tmp/module/".replace( "~", System.getProperty( "user.home" ) );
        new ModuleExporter().exportModuleToZip( module, Paths.get( exportFile ) );
    }
}
