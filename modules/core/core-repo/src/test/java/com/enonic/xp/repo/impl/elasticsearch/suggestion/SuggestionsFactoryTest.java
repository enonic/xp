package com.enonic.xp.repo.impl.elasticsearch.suggestion;

import java.util.List;

import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.term.TermSuggestion;
import org.elasticsearch.search.suggest.term.TermSuggestion.Entry;
import org.elasticsearch.search.suggest.term.TermSuggestion.Entry.Option;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.repo.impl.elasticsearch.suggistion.SuggestionsFactory;
import com.enonic.xp.suggester.Suggestions;
import com.enonic.xp.suggester.TermSuggestionEntry;
import com.enonic.xp.suggester.TermSuggestionOption;

public class SuggestionsFactoryTest
{
    private SuggestionsFactory suggestionsFactory;

    @Before
    public void init()
    {
        suggestionsFactory = new SuggestionsFactory();
    }

    @Test
    public void testEmpty()
    {
        final Suggest suggest = new Suggest();
        final Suggestions suggestions = SuggestionsFactory.create( suggest );

        Assert.assertNotNull( suggestions );
        Assert.assertTrue( suggestions.isEmpty() );
    }

    @Test
    public void testTermSuggestion()
    {
        final Option option1 = Mockito.mock( Option.class );
        Mockito.when( option1.getText() ).thenReturn( new Text( "option1" ) );
        Mockito.when( option1.getScore() ).thenReturn( 1.0f );
        Mockito.when( option1.getFreq() ).thenReturn( 3 );

        final Entry entry1 = Mockito.mock( Entry.class );
        Mockito.when( entry1.getLength() ).thenReturn( 2 );
        Mockito.when( entry1.getOffset() ).thenReturn( 1 );
        Mockito.when( entry1.getText() ).thenReturn( new Text( "entry1" ) );
        Mockito.when( entry1.getOptions() ).thenReturn( List.of( option1 ) );

        final TermSuggestion termSuggestion1 = Mockito.mock( TermSuggestion.class );
        Mockito.when( termSuggestion1.getName() ).thenReturn( "suggestion1" );
        Mockito.when( termSuggestion1.getEntries() ).thenReturn( List.of( entry1 ) );

        final Suggest suggest = new Suggest( List.of( termSuggestion1 ) );
        final Suggestions suggestions = SuggestionsFactory.create( suggest );

        Assert.assertNotNull( suggestions );
        Assert.assertFalse( suggestions.isEmpty() );

        final com.enonic.xp.suggester.TermSuggestion resultSuggestion =
            (com.enonic.xp.suggester.TermSuggestion) suggestions.get( "suggestion1" );
        Assert.assertFalse( resultSuggestion.getEntries().isEmpty() );

        final TermSuggestionEntry resultEntry = resultSuggestion.getEntries().get( 0 );
        Assert.assertEquals( "entry1", resultEntry.getText() );
        Assert.assertEquals( 2L, resultEntry.getLength().longValue() );
        Assert.assertEquals( 1L, resultEntry.getOffset().longValue() );

        final TermSuggestionOption resultOption = resultEntry.getOptions().get( 0 );
        Assert.assertEquals( "option1", resultOption.getText() );
        Assert.assertEquals( 3L, resultOption.getFreq().longValue() );
        Assert.assertEquals( 1.0f, resultOption.getScore(), 1e-14 );
    }
}
