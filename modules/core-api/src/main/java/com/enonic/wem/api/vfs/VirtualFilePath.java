package com.enonic.wem.api.vfs;

import java.nio.file.Path;
import java.util.LinkedList;

public interface VirtualFilePath
{
    public VirtualFilePath subtractPath( final VirtualFilePath subtract );

    public String getPath();

    public LinkedList<String> getElements();

    public String getName();

    public VirtualFilePath join( final VirtualFilePathImpl... paths );

    public VirtualFilePath join( final String... elements );

    public Path toLocalPath();

    public int size();


}
