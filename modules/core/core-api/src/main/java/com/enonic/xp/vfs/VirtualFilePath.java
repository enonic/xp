package com.enonic.xp.vfs;

import java.nio.file.Path;
import java.util.LinkedList;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface VirtualFilePath
{
    VirtualFilePath subtractPath( VirtualFilePath subtract );

    String getPath();

    LinkedList<String> getElements();

    String getName();

    @Deprecated
    VirtualFilePath join( VirtualFilePathImpl... paths );

    VirtualFilePath join( String... elements );

    Path toLocalPath();

    int size();
}
