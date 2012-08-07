package com.enonic.wem.web.rest2.service.upload;

import java.io.File;

public interface UploadItem
{
    public String getId();

    public String getName();

    public String getMimeType();

    public long getUploadTime();

    public long getSize();

    public File getFile();
}
