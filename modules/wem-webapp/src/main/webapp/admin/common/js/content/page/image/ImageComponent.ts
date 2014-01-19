module api.content.page.image {

    export class ImageComponent extends api.content.page.PageComponent<ImageTemplateKey> {

        private image: api.content.ContentId;

        constructor(builder?: ImageComponentBuilder) {
            super(builder);
            if (builder) {
                this.image = builder.image;
            }
        }

        getImage(): api.content.ContentId {
            return this.image;
        }

        setImage(value: api.content.ContentId) {
            this.image = value;
        }

        toJson(): json.ImageComponentJson {
            var json:json.ImageComponentJson = <json.ImageComponentJson>super.toJson();
            json.image = this.image.toString();
            return json;
        }
    }

    export class ImageComponentBuilder extends api.content.page.PageComponentBuilder<ImageTemplateKey> {

        image: api.content.ContentId;

        public fromJson(json: json.ImageComponentJson): ImageComponentBuilder {
            this.setTemplate(ImageTemplateKey.fromString(json.template));
            this.setName(new api.content.page.ComponentName(json.name));
            this.setConfig(api.data.DataFactory.createRootDataSet(json.config));
            if (json.image) {
                this.setImage(new api.content.ContentId(json.image));
            }
            return this;
        }

        public setImage(value: api.content.ContentId): ImageComponentBuilder {
            this.image = value;
            return this;
        }

        public build(): ImageComponent {
            return new ImageComponent(this);
        }
    }
}