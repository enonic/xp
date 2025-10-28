package com.enonic.xp.portal.impl.html;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HtmlBuilderTest
{

    @Test
    void test()
    {
        final HtmlBuilder builder = new HtmlBuilder();
        builder.open( "div" ).open( "br" ).closeEmpty().open( "br" ).closeEmpty().text( "Text" ).open( "br" ).closeEmpty().close();

        assertEquals( "<div><br/><br/>Text<br/></div>", builder.toString() );
    }

    @Test
    void testWithAttribute()
    {
        final HtmlBuilder builder = new HtmlBuilder();
        builder.open( "div" ).attribute( "data-config", "value" ).text( "Text" ).close();

        assertEquals( "<div data-config=\"value\">Text</div>", builder.toString() );
    }

}
