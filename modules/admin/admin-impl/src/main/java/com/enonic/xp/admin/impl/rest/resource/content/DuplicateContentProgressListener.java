package com.enonic.xp.admin.impl.rest.resource.content;

import com.enonic.xp.content.DuplicateContentListener;
import com.enonic.xp.task.ProgressReporter;

public final class DuplicateContentProgressListener
    implements DuplicateContentListener
{
    private static final float DUPLICATE_WEIGHT = 0.88f;

    private static final float REFERENCES_WEIGHT = 0.12f;

    private final ProgressReporter progressReporter;

    private int total = 0;

    private float progressCount = 0;

    public DuplicateContentProgressListener( final ProgressReporter progressReporter )
    {
        this.progressReporter = progressReporter;
    }

    @Override
    public void setTotal( final int count )
    {
        total = count;
    }

    @Override
    public void contentDuplicated( final int count )
    {
        progressCount += count * DUPLICATE_WEIGHT;
        progressReporter.progress( Math.round( progressCount ), total );
    }

    @Override
    public void contentReferencesUpdated( final int count )
    {
        progressCount += count * REFERENCES_WEIGHT;
        progressReporter.progress( Math.round( progressCount ), total );
    }
}
