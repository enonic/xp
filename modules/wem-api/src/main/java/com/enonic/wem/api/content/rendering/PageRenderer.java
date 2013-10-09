package com.enonic.wem.api.content.rendering;


import com.enonic.wem.api.content.Content;

public class PageRenderer
{
    private Page page;

    void render( Content content )
    {
        String result = page.getController().execute( content );

        PlaceholderProcessor placeholderProcessor = new PlaceholderProcessor();
        //placeholderProcessor.processPlaceHolders( result );
    }

}
