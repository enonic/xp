module api.content.page.image {

    import Region = api.content.page.region.Region;

    export class ImageComponent extends api.content.page.DescriptorBasedPageComponent implements api.Equitable, api.Cloneable {

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

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, ImageComponent)) {
                return false;
            }

            var other = <ImageComponent>o;

            if (!super.equals(o)) {
                return false;
            }

            if (!api.ObjectHelper.equals(this.image, other.image)) {
                return false;
            }

            return true;
        }

        clone(): ImageComponent {
            return new ImageComponentBuilder(this).build();
        }
    }

    export class ImageComponentBuilder extends api.content.page.DescriptorBasedPageComponentBuilder<ImageComponent> {

        image: api.content.ContentId;

        constructor(source?: ImageComponent) {

            super(source);
            if (source) {
                this.image = source.getImage();
            }
        }

        public fromJson(json: ImageComponentJson, region: Region): ImageComponentBuilder {

            if (json.image) {
                this.setImage(new api.content.ContentId(json.image));
            }

            this.setName(new api.content.page.ComponentName(json.name));

            if (json.descriptor) {
                this.setDescriptor(api.content.page.DescriptorKey.fromString(json.descriptor));
            }

            this.setConfig(api.data.DataFactory.createRootDataSet(json.config));
            this.setParent(region);

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