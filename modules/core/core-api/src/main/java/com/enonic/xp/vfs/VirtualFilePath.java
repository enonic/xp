package com.enonic.xp.vfs;

import java.nio.file.Path;
import java.util.List;

import com.google.common.annotations.Beta;

@Beta
public interface VirtualFilePath
{
    VirtualFilePath subtractPath( final VirtualFilePath subtract );

    String getPath();

    List<String> getElements();

    String getName();

    VirtualFilePath join( final VirtualFilePathImpl... paths );

    VirtualFilePath join( final String... elements );

    Path toLocalPath();

    int size();
}
