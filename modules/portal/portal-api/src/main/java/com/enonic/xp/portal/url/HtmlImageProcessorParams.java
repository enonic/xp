package com.enonic.xp.portal.url;

import java.util.List;
import java.util.Map;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.html.HtmlElement;
import com.enonic.xp.style.ImageStyle;

public class HtmlImageProcessorParams
{
    private final HtmlElement element;

    private final String contentId;

    private final ImageStyle imageStyle;

    private final String type;

    private final Map<String, String> queryParams;

    private final String mode;

    private final List<Integer> imageWidths;

    private final PortalRequest portalRequest;

    private final String imageSizes;

    private final Runnable defaultProcessor;

    private HtmlImageProcessorParams( Builder builder )
    {
        this.element = builder.element;
        this.contentId = builder.contentId;
        this.imageStyle = builder.imageStyle;
        this.type = builder.type;
        this.queryParams = builder.queryParams;
        this.mode = builder.mode;
        this.imageWidths = builder.imageWidths;
        this.imageSizes = builder.imageSizes;
        this.portalRequest = builder.portalRequest;
        this.defaultProcessor = builder.defaultProcessor;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public HtmlElement getElement()
    {
        return element;
    }

    public String getContentId()
    {
        return contentId;
    }

    public ImageStyle getImageStyle()
    {
        return imageStyle;
    }

    public String getType()
    {
        return type;
    }

    public Map<String, String> getQueryParams()
    {
        return queryParams;
    }

    public String getMode()
    {
        return mode;
    }

    public List<Integer> getImageWidths()
    {
        return imageWidths;
    }

    public String getImageSizes()
    {
        return imageSizes;
    }

    public PortalRequest getPortalRequest()
    {
        return portalRequest;
    }

    public Runnable getDefaultProcessor()
    {
        return defaultProcessor;
    }

    public void makeDefault()
    {
        if ( defaultProcessor != null )
        {
            defaultProcessor.run();
        }
    }

    public static class Builder
    {
        private HtmlElement element;

        private String contentId;

        private ImageStyle imageStyle;

        private String type;

        private Map<String, String> queryParams;

        private String mode;

        private List<Integer> imageWidths;

        private String imageSizes;

        private PortalRequest portalRequest;

        private Runnable defaultProcessor;

        public Builder setElement( final HtmlElement element )
        {
            this.element = element;
            return this;
        }

        public Builder setContentId( final String contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public Builder setImageStyle( final ImageStyle imageStyle )
        {
            this.imageStyle = imageStyle;
            return this;
        }

        public Builder setDefaultProcessor( final Runnable defaultProcessor )
        {
            this.defaultProcessor = defaultProcessor;
            return this;
        }

        public Builder setType( final String type )
        {
            this.type = type;
            return this;
        }

        public Builder setQueryParams( final Map<String, String> queryParams )
        {
            this.queryParams = queryParams;
            return this;
        }

        public Builder setMode( final String mode )
        {
            this.mode = mode;
            return this;
        }

        public Builder setImageWidths( final List<Integer> imageWidths )
        {
            this.imageWidths = imageWidths;
            return this;
        }

        public Builder setImageSizes( final String imageSizes )
        {
            this.imageSizes = imageSizes;
            return this;
        }

        public Builder setPortalRequest( final PortalRequest portalRequest )
        {
            this.portalRequest = portalRequest;
            return this;
        }

        public HtmlImageProcessorParams build()
        {
            return new HtmlImageProcessorParams( this );
        }
    }
}
