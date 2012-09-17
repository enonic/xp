package com.enonic.wem.core.content.type.formitem.comptype;

import com.enonic.wem.core.content.data.Data;

/**
 * TODO
 */
public class HtmlAreaConfig
    implements ComponentTypeConfig
{

    @Override
    public void checkValidity( final Data data )
    {

    }


    public static Builder newHtmlAreaConfig()
    {
        return new Builder();
    }

    public static class Builder
    {
        Builder()
        {
            // protection
        }

        public HtmlAreaConfig build()
        {
            return new HtmlAreaConfig();
        }
    }
}
