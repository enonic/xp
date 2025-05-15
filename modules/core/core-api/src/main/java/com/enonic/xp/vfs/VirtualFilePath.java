package com.enonic.xp.vfs;

import java.nio.file.Path;
import java.util.List;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface VirtualFilePath
{
    VirtualFilePath subtractPath( VirtualFilePath subtract );

    String getPath();

    List<String> getElements();

    String getName();

    VirtualFilePath join( String... elements );

    Path toLocalPath();

    int size();
}
