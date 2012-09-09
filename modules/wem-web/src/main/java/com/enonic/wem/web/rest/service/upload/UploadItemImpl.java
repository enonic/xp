package com.enonic.wem.web.rest.service.upload;

import java.io.File;

final class UploadItemImpl
    implements UploadItem
{
    private final File file;

    private final String name;

    private final String mimeType;

    private final long uploadTime;

    public UploadItemImpl( final File file, final String name, final String mimeType )
    {
        this.file = file;
        this.name = name;
        this.mimeType = mimeType;
        this.uploadTime = System.currentTimeMillis();
    }

    @Override
    public String getId()
    {
        return this.file.getName();
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public String getMimeType()
    {
        return this.mimeType;
    }

    @Override
    public long getUploadTime()
    {
        return this.uploadTime;
    }

    @Override
    public long getSize()
    {
        return this.file.length();
    }

    @Override
    public File getFile()
    {
        return this.file;
    }
}
