package com.enonic.wem.itests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

import junit.framework.Assert;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class SimpleTest
    extends AbstractTest
{
    @Test
    public void testFramework()
    {
        Assert.assertTrue( true );
    }
}
