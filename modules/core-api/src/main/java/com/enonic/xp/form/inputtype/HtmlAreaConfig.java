package com.enonic.xp.form.inputtype;

import com.enonic.xp.data.Property;

/**
 * TODO
 */
public class HtmlAreaConfig
    implements InputTypeConfig
{

    @Override
    public void checkValidity( final Property property )
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
