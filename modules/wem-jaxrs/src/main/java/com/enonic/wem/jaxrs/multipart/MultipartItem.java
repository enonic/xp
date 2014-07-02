package com.enonic.wem.jaxrs.multipart;

import java.io.IOException;
import java.io.InputStream;

public interface MultipartItem
{
    public String getName();

    public String getContentType();

    public long getSize();

    public String getFieldName();

    public InputStream getInputStream()
        throws IOException;

    public boolean isFormField();

    public void delete();
}
