module api.content.page.image {

    export class ImageComponent extends api.content.page.PageComponent {

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

        toJson(): api.content.page.PageComponentTypeWrapperJson {

            var json: ImageComponentJson = <ImageComponentJson>super.toPageComponentJson();
            json.image = this.image != null ? this.image.toString() : null;

            return <api.content.page.PageComponentTypeWrapperJson> {
                ImageComponent: json
            };
        }
    }

    export class ImageComponentBuilder extends api.content.page.PageComponentBuilder<ImageComponent> {

        image: api.content.ContentId;

        public fromJson(json: ImageComponentJson, regionPath: RegionPath): ImageComponentBuilder {

            if (json.image) {
                this.setImage(new api.content.ContentId(json.image));
            }

            this.setName(new api.content.page.ComponentName(json.name));

            if (json.descriptor) {
                this.setDescriptor(api.content.page.DescriptorKey.fromString(json.descriptor));
            }

            this.setConfig(api.data.DataFactory.createRootDataSet(json.config));
            this.setRegion(regionPath);

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