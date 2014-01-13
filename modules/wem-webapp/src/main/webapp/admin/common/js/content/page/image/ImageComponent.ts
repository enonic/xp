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

        public fromJson(json: json.ImageComponentJson): ImageComponentBuilder {
            this.setTemplate(ImageTemplateKey.fromString(json.template));
            this.setName(new api.content.page.ComponentName(json.name));
            this.setConfig(api.data.DataFactory.createRootDataSet(json.config));
            if (json.image) {
                this.setImageContent(new api.content.ContentId(json.image));
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