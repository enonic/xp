package com.enonic.xp.portal.impl.handler.attachment;

final class Range
{
    final long start;

    final long end;

    final long length;

    public Range( final long start, final long end )
    {
        this.start = start;
        this.end = end;
        this.length = end - start + 1;
    }

}
