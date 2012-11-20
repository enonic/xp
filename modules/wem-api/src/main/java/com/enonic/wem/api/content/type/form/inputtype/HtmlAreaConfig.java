package com.enonic.wem.api.content.type.form.inputtype;

import com.enonic.wem.api.content.data.Data;

/**
 * TODO
 */
public class HtmlAreaConfig
    implements InputTypeConfig
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
