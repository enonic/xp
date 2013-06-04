package com.enonic.wem.portal.request;

import com.google.common.base.Objects;

public class AttachmentRequest
{
    private String label;

    private String key;

    private String attachmentName;

    public String getAttachmentName()
    {
        return attachmentName;
    }

    public void setAttachmentName( final String attachmentName )
    {
        this.attachmentName = attachmentName;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel( final String label )
    {
        this.label = label;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey( final String key )
    {
        this.key = key;
    }


    @Override
    public String toString()
    {
        return Objects.toStringHelper( this ).
            add( "Key", this.key ).
            add( "Label", this.label ).
            add( "AttachmentName", this.attachmentName ).
            omitNullValues().
            toString();
    }

}
