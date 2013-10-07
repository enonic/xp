package com.enonic.wem.api.module;

import java.io.File;

import org.junit.Test;

import com.enonic.wem.api.schema.content.QualifiedContentTypeName;

import static com.enonic.wem.api.module.ModuleFileEntry.directoryBuilder;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class ModuleTest
{
    @Test
    public void testCreateModule()
    {
        final ModuleFileEntry publicDir = directoryBuilder( "public" ).
            add( new File( "/modules/mymodule/public/file1.txt" ) ).
            build();
        final ModuleFileEntry templatesDir = directoryBuilder( "templates" ).
            add( new File( "/modules/mymodule/templates/mytemplate.tpl" ) ).
            build();

        final Module module = Module.newModule().
            moduleKey( ModuleKey.parse( "mymodule-1.0.0" ) ).
            displayName( "module display name" ).
            info( "module-info" ).
            url( "http://enonic.net" ).
            vendorName( "Enonic" ).
            vendorUrl( "https://www.enonic.com" ).
            minSystemVersion( ModuleVersion.from( 5, 0, 0 ) ).
            maxSystemVersion( ModuleVersion.from( 6, 0, 0 ) ).
            addModuleDependency( ModuleKey.parse( "modulefoo-1.0.0" ) ).
            addContentTypeDependency( QualifiedContentTypeName.from( "article" ) ).

            addFileEntry( publicDir ).
            addFileEntry( templatesDir ).
            addFileEntry( directoryBuilder( "emptydir" ).build() ).
            build();

        assertEquals( "mymodule-1.0.0", module.getModuleKey().toString() );
        assertEquals( "module display name", module.getDisplayName() );
        assertEquals( "module-info", module.getInfo() );
        assertEquals( "http://enonic.net", module.getUrl() );
        assertEquals( "Enonic", module.getVendorName() );
        assertEquals( "https://www.enonic.com", module.getVendorUrl() );
        assertEquals( "5.0.0", module.getMinSystemVersion().toString() );
        assertEquals( "6.0.0", module.getMaxSystemVersion().toString() );
        assertTrue( module.getContentTypeDependencies().contains( QualifiedContentTypeName.from( "article" ) ) );
        assertTrue( module.getModuleDependencies().contains( ModuleKey.parse( "modulefoo-1.0.0" ) ) );
    }
}
