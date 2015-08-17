package com.enonic.xp.data;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.MixinName;

public class BuildPropertyTreeParams
{

    private JsonNode jsonTree;

    private ContentTypeName contentTypeName;

    private MixinName mixinName;

    private BuildPropertyTreeParams( final Builder builder )
    {
        this.jsonTree = builder.jsonTree;
        this.contentTypeName = builder.contentTypeName;
        this.mixinName = builder.mixinName;
    }

    public JsonNode getJsonTree()
    {
        return jsonTree;
    }

    public ContentTypeName getContentTypeName()
    {
        return contentTypeName;
    }

    public MixinName getMixinName()
    {
        return mixinName;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private JsonNode jsonTree;

        private ContentTypeName contentTypeName;

        private MixinName mixinName;

        public Builder jsonTree( final JsonNode jsonTree )
        {
            this.jsonTree = jsonTree;
            return this;
        }

        public Builder contentTypeName( final ContentTypeName contentTypeName )
        {
            this.contentTypeName = contentTypeName;
            return this;
        }

        public Builder mixinName( final MixinName mixinName )
        {
            this.mixinName = mixinName;
            return this;
        }

        public BuildPropertyTreeParams build()
        {
            return new BuildPropertyTreeParams( this );
        }
    }
}
