package com.enonic.wem.api.module;

import java.io.File;

import org.junit.Test;

import static com.enonic.wem.api.module.ModuleFileEntry.directoryBuilder;
import static junit.framework.Assert.assertEquals;

public class ModuleTest
{
    @Test
    public void testCreateModule()
    {
        final ModuleFileEntry publicDir = directoryBuilder( "public" ).
            add( new File( "/modules/mymodule/public/file1.txt" ) ).
            build();

        final Module module = Module.newModule().
            displayName( "module display name" ).
            addFileEntry( publicDir ).
            build();

        assertEquals( "module display name", module.getDisplayName() );
    }
}
