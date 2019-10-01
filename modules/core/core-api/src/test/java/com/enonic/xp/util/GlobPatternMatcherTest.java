package com.enonic.xp.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GlobPatternMatcherTest
{
    @Test
    public void exact()
        throws Exception
    {
        assertTrue( GlobPatternMatcher.match( "e1.fisk", "e1.fisk", "." ) );
        assertTrue( GlobPatternMatcher.match( "e1/fisk", "e1/fisk", "/" ) );
    }

    @Test
    public void exact_absolute()
        throws Exception
    {
        assertTrue( GlobPatternMatcher.match( "/e1.fisk", "/e1.fisk", "." ) );
        assertTrue( GlobPatternMatcher.match( "/e1/fisk", "/e1/fisk", "/" ) );
    }


    @Test
    public void wildcard_last()
        throws Exception
    {
        assertTrue( GlobPatternMatcher.match( "e1.*", "e1.fisk", "." ) );
    }

    @Test
    public void wildcard_between()
        throws Exception
    {
        assertTrue( GlobPatternMatcher.match( "e1.*.fisk", "e1.ost.fisk", "." ) );
    }

    @Test
    public void wildcard_between_no_match_1()
        throws Exception
    {
        assertFalse( GlobPatternMatcher.match( "e1.*.fisk", "e1.ost.løk.fisk", "." ) );
        assertFalse( GlobPatternMatcher.match( "e1/*/fisk", "e1/ost/løk/fisk", "/" ) );
    }

    @Test
    public void two_wildcard_match()
        throws Exception
    {
        assertTrue( GlobPatternMatcher.match( "e1.*.*.fisk", "e1.pølse.løk.fisk", "." ) );
        assertTrue( GlobPatternMatcher.match( "e1/*/*/fisk", "e1/pølse/løk/fisk", "/" ) );
    }

    @Test
    public void two_wildcard_no_match()
        throws Exception
    {
        assertFalse( GlobPatternMatcher.match( "e1.*.*.fisk", "e1.ost.pølse.løk.fisk", "." ) );
        assertFalse( GlobPatternMatcher.match( "e1/*/*/fisk", "e1/ost/pølse/løk/fisk", "/" ) );
    }

    @Test
    public void double_wildcard_match()
        throws Exception
    {
        assertTrue( GlobPatternMatcher.match( "e1.**.fisk", "e1.ost.pølse.løk.fisk", "." ) );
        assertTrue( GlobPatternMatcher.match( "e1/**/fisk", "e1/ost/pølse/løk/fisk", "/" ) );
    }


    @Test
    public void double_wildcard_no_match()
        throws Exception
    {
        assertFalse( GlobPatternMatcher.match( "e1.**.fisk", "e1.ost.pølse.løk.fisk.ost", "." ) );
        assertFalse( GlobPatternMatcher.match( "e1/**/fisk", "e1/ost/pølse/løk/fisk/ost", "/" ) );
    }

    @Test
    public void double_wildcard_singel_wildcard_combo()
        throws Exception
    {
        assertTrue( GlobPatternMatcher.match( "e1.**.fisk.*", "e1.ost.pølse.løk.fisk.ost", "." ) );
    }

    @Test
    public void double_wildcard_at_end()
        throws Exception
    {
        assertTrue( GlobPatternMatcher.match( "e1.fisk.**", "e1.fisk.ost.løk", "." ) );
    }


    @Test
    public void testName()
        throws Exception
    {
        assertTrue( GlobPatternMatcher.match( "page.regions.**.text", "page.regions.main.components.regions.fisk.components.text", "." ) );
    }
}
