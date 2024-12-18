package com.enonic.xp.repo.impl.vacuum.blob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.blob.Segment;
import com.enonic.xp.blob.SegmentLevel;
import com.enonic.xp.repo.impl.node.NodeConstants;
import com.enonic.xp.repo.impl.vacuum.VacuumTask;


public class NodeBlobVacuumTaskTest
    extends AbstractBlobVacuumTaskTest
{
    @Override
    @BeforeEach
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.segment = Segment.from( SegmentLevel.from( "test" ), NodeConstants.NODE_SEGMENT_LEVEL );
    }

    @Override
    @Test
    public void test_delete_unused()
        throws Exception
    {
        super.test_delete_unused();
    }

    @Override
    @Test
    public void test_progress_report()
        throws Exception
    {
        super.test_progress_report();
    }

    @Override
    @Test
    public void age_threshold()
        throws Exception
    {
        super.age_threshold();
    }

    @Override
    protected VacuumTask createTask()
    {
        return new NodeBlobVacuumTask( this.nodeService, this.blobStore );
    }
}
