package com.enonic.xp.content.attachment.image;


public enum ImageAttachmentScale
{
    SMALL( "small", 256 ),
    MEDIUM( "medium", 512 ),
    LARGE( "large", 1024 ),
    EXTRA_LARGE( "extra-large", 2048 );

    private final String label;

    private final int size;

    private ImageAttachmentScale( final String label, final int size )
    {
        this.label = label;
        this.size = size;
    }


    public String getLabel()
    {
        return this.label;
    }

    public int getSize()
    {
        return this.size;
    }

    public static ImageAttachmentScale[] getScalesOrderedBySizeAsc() {
        return new ImageAttachmentScale[] { SMALL, MEDIUM, LARGE, EXTRA_LARGE};
    }
}
