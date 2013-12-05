module api_content_page_image {

    export class ImageComponent extends api_content_page.BasePageComponent<ImageTemplateKey> {

        private config: api_data.RootDataSet;

        private imageContent: api_content.ContentId;

        constructor(builder?: ImageComponentBuilder) {
            super(builder);
            this.config = builder.config;
            this.imageContent = builder.imageContent;
        }

        getConfig(): api_data.RootDataSet {
            return this.config;
        }

        getImageContentId(): api_content.ContentId {
            return this.imageContent;
        }

        setConfig(value: api_data.RootDataSet) {
            this.config = value;
        }

        setImageContent(value: api_content.ContentId) {
            this.imageContent = value;
        }
    }

    export class ImageComponentBuilder extends api_content_page.BaseComponentBuilder<ImageTemplateKey> {

        config: api_data.RootDataSet;

        imageContent: api_content.ContentId;

        public fromDataSet(dataSet: api_data.DataSet): ImageComponentBuilder {
            this.setTemplate(ImageTemplateKey.fromString(dataSet.getProperty("template").getString()));
            var imageContentProperty = dataSet.getProperty("image");
            if (imageContentProperty != null) {
                this.imageContent = new api_content.ContentId(imageContentProperty.getString());
            }
            var configProperty = dataSet.getProperty("config");
            if (configProperty != null) {
                this.config = configProperty.getValue().asRootDataSet();
            }
            return this;
        }

        public setConfig(value: api_data.RootDataSet): ImageComponentBuilder {
            this.config = value;
            return this;
        }

        public setImageContent(value: api_content.ContentId): ImageComponentBuilder {
            this.imageContent = value;
            return this;
        }

        public build(): ImageComponent {
            return new ImageComponent(this);
        }
    }
}