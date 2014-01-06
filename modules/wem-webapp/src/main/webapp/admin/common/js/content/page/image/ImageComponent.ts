module api.content.page.image {

    export class ImageComponent extends api.content.page.PageComponent<ImageTemplateKey> {

        private config: api.data.RootDataSet;

        private imageContent: api.content.ContentId;

        constructor(builder?: ImageComponentBuilder) {
            super(builder);
            if (builder) {
                this.config = builder.config;
                this.imageContent = builder.imageContent;
            }
        }

        getConfig(): api.data.RootDataSet {
            return this.config;
        }

        getImageContentId(): api.content.ContentId {
            return this.imageContent;
        }

        setConfig(value: api.data.RootDataSet) {
            this.config = value;
        }

        setImageContent(value: api.content.ContentId) {
            this.imageContent = value;
        }
    }

    export class ImageComponentBuilder extends api.content.page.ComponentBuilder<ImageTemplateKey> {

        config: api.data.RootDataSet;

        imageContent: api.content.ContentId;

        public fromDataSet(dataSet: api.data.DataSet): ImageComponentBuilder {
            this.setTemplate(ImageTemplateKey.fromString(dataSet.getProperty("template").getString()));
            var imageContentProperty = dataSet.getProperty("image");
            if (imageContentProperty != null) {
                this.imageContent = new api.content.ContentId(imageContentProperty.getString());
            }
            var configProperty = dataSet.getProperty("config");
            if (configProperty != null) {
                this.config = configProperty.getValue().asRootDataSet();
            }
            return this;
        }

        public setConfig(value: api.data.RootDataSet): ImageComponentBuilder {
            this.config = value;
            return this;
        }

        public setImageContent(value: api.content.ContentId): ImageComponentBuilder {
            this.imageContent = value;
            return this;
        }

        public build(): ImageComponent {
            return new ImageComponent(this);
        }
    }
}