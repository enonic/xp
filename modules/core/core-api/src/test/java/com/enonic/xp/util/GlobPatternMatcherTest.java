package com.enonic.xp.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GlobPatternMatcherTest
{
    @Test
    void exact()
    {
        assertTrue( GlobPatternMatcher.match( "e1.fisk", "e1.fisk", "." ) );
        assertTrue( GlobPatternMatcher.match( "e1/fisk", "e1/fisk", "/" ) );
    }

    @Test
    void exact_absolute()
    {
        assertTrue( GlobPatternMatcher.match( "/e1.fisk", "/e1.fisk", "." ) );
        assertTrue( GlobPatternMatcher.match( "/e1/fisk", "/e1/fisk", "/" ) );
    }


    @Test
    void wildcard_last()
    {
        assertTrue( GlobPatternMatcher.match( "e1.*", "e1.fisk", "." ) );
    }

    @Test
    void wildcard_between()
    {
        assertTrue( GlobPatternMatcher.match( "e1.*.fisk", "e1.ost.fisk", "." ) );
    }

    @Test
    void wildcard_between_no_match_1()
    {
        assertFalse( GlobPatternMatcher.match( "e1.*.fisk", "e1.ost.løk.fisk", "." ) );
        assertFalse( GlobPatternMatcher.match( "e1/*/fisk", "e1/ost/løk/fisk", "/" ) );
    }

    @Test
    void two_wildcard_match()
    {
        assertTrue( GlobPatternMatcher.match( "e1.*.*.fisk", "e1.pølse.løk.fisk", "." ) );
        assertTrue( GlobPatternMatcher.match( "e1/*/*/fisk", "e1/pølse/løk/fisk", "/" ) );
    }

    @Test
    void two_wildcard_no_match()
    {
        assertFalse( GlobPatternMatcher.match( "e1.*.*.fisk", "e1.ost.pølse.løk.fisk", "." ) );
        assertFalse( GlobPatternMatcher.match( "e1/*/*/fisk", "e1/ost/pølse/løk/fisk", "/" ) );
    }

    @Test
    void double_wildcard_match()
    {
        assertTrue( GlobPatternMatcher.match( "e1.**.fisk", "e1.ost.pølse.løk.fisk", "." ) );
        assertTrue( GlobPatternMatcher.match( "e1/**/fisk", "e1/ost/pølse/løk/fisk", "/" ) );
    }


    @Test
    void double_wildcard_no_match()
    {
        assertFalse( GlobPatternMatcher.match( "e1.**.fisk", "e1.ost.pølse.løk.fisk.ost", "." ) );
        assertFalse( GlobPatternMatcher.match( "e1/**/fisk", "e1/ost/pølse/løk/fisk/ost", "/" ) );
    }

    @Test
    void double_wildcard_singel_wildcard_combo()
    {
        assertTrue( GlobPatternMatcher.match( "e1.**.fisk.*", "e1.ost.pølse.løk.fisk.ost", "." ) );
    }

    @Test
    void double_wildcard_at_end()
    {
        assertTrue( GlobPatternMatcher.match( "e1.fisk.**", "e1.fisk.ost.løk", "." ) );
    }


    @Test
    void testName()
    {
        assertTrue( GlobPatternMatcher.match( "page.regions.**.text", "page.regions.main.components.regions.fisk.components.text", "." ) );
    }
}
