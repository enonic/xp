package com.enonic.wem.api.module;

import com.google.common.io.ByteSource;

// TODO: Rename to Resource
public interface ModuleFile
{
    public ModuleFileKey getKey();

    public ModuleKey getModule();

    public String getUri();

    public String getName();

    public String getPath();

    public String getExtension();

    public ModuleFile getParent();

    public ModuleFile resolve( String path );

    public boolean isRoot();

    public Iterable<ModuleFile> getChildren();

    public long getTimestamp();

    public ByteSource getBytes();

    public long getSize();

    public boolean isFolder();

    public String getMimeType();

    public boolean exists();
}
