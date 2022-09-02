package com.enonic.xp.portal.url;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.portal.html.HtmlDocument;
import com.enonic.xp.portal.html.HtmlElement;

@PublicApi
public final class HtmlProcessorParams
{
    private final HtmlDocument htmlDocument;

    private final Consumer<HtmlElementPostProcessor> defaultProcessor;

    private final BiConsumer<HtmlElement, HtmlElementPostProcessor> defaultElementProcessor;

    private HtmlProcessorParams( Builder builder )
    {
        this.htmlDocument = builder.htmlDocument;
        this.defaultProcessor = Objects.requireNonNull( builder.defaultProcessor );
        this.defaultElementProcessor = Objects.requireNonNull( builder.defaultElementProcessor );
    }

    public HtmlDocument getDocument()
    {
        return htmlDocument;
    }

    public void processDefault()
    {
        defaultProcessor.accept( null );
    }

    public void processDefault( HtmlElementPostProcessor elementProcessor )
    {
        defaultProcessor.accept( elementProcessor );
    }

    public void processElementDefault( HtmlElement element )
    {
        defaultElementProcessor.accept( element, null );
    }

    public void processElementDefault( HtmlElement element, HtmlElementPostProcessor elementProcessor )
    {
        defaultElementProcessor.accept( element, elementProcessor );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private HtmlDocument htmlDocument;

        private Consumer<HtmlElementPostProcessor> defaultProcessor;

        private BiConsumer<HtmlElement, HtmlElementPostProcessor> defaultElementProcessor;

        private Builder()
        {
        }

        public Builder htmlDocument( final HtmlDocument htmlDocument )
        {
            this.htmlDocument = htmlDocument;
            return this;
        }

        public Builder defaultProcessor( final Consumer<HtmlElementPostProcessor> defaultProcessor )
        {
            this.defaultProcessor = defaultProcessor;
            return this;
        }

        public Builder defaultElementProcessor( final BiConsumer<HtmlElement, HtmlElementPostProcessor> defaultElementProcessor )
        {
            this.defaultElementProcessor = defaultElementProcessor;
            return this;
        }

        public HtmlProcessorParams build()
        {
            return new HtmlProcessorParams( this );
        }
    }
}
