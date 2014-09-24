module api.content.page.image {

    import Region = api.content.page.region.Region;
    import Form = api.form.Form;
    import FormBuilder = api.form.FormBuilder;
    import OccurrencesBuilder = api.form.OccurrencesBuilder;
    import TextLine = api.form.inputtype.text.TextLine;
    import TextArea = api.form.inputtype.text.TextArea;
    import RootDataSet = api.data.RootDataSet;

    export class ImageComponent extends api.content.page.PageComponent implements api.Equitable, api.Cloneable {

        private image: api.content.ContentId;

        private config: RootDataSet;

        private form: Form;

        constructor(builder?: ImageComponentBuilder) {
            super(builder);
            if (builder) {
                this.image = builder.image;
                this.config = builder.config;
            }
            var formBuilder = new FormBuilder();
            formBuilder.addFormItem(new api.form.InputBuilder().
                setName("caption").
                setInputType(TextArea.getName()).
                setLabel("Caption").
                setOccurrences(new OccurrencesBuilder().setMinimum(0).setMaximum(1).build()).
                build());
            this.form = formBuilder.build();
        }

        getImage(): api.content.ContentId {
            return this.image;
        }

        getForm(): api.form.Form {
            return this.form;
        }

        getConfig(): api.data.RootDataSet {
            return this.config;
        }

        setConfig(value: api.data.RootDataSet) {
            this.config = value;
        }

        setImage(value: api.content.ContentId) {
            this.image = value;
        }

        toJson(): api.content.page.PageComponentTypeWrapperJson {

            var json: ImageComponentJson = <ImageComponentJson>super.toPageComponentJson();
            json.image = this.image != null ? this.image.toString() : null;
            json.config = this.config != null ? this.config.toJson() : null;

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

            if (!api.ObjectHelper.equals(this.config, other.config)) {
                return false;
            }

            return true;
        }

        clone(): ImageComponent {
            return new ImageComponentBuilder(this).build();
        }
    }

    export class ImageComponentBuilder extends api.content.page.PageComponentBuilder<ImageComponent> {

        image: api.content.ContentId;

        config: RootDataSet;

        constructor(source?: ImageComponent) {
            super(source);
            if (source) {
                this.image = source.getImage();
                this.config = source.getConfig();
            }
        }

        public setImage(value: api.content.ContentId): ImageComponentBuilder {
            this.image = value;
            return this;
        }

        public setConfig(value: api.data.RootDataSet): ImageComponentBuilder {
            this.config = value;
            return this;
        }

        public fromJson(json: ImageComponentJson, region: Region): ImageComponentBuilder {

            if (json.image) {
                this.setImage(new api.content.ContentId(json.image));
            }

            this.setName(new api.content.page.ComponentName(json.name));


            this.setConfig(api.data.DataFactory.createRootDataSet(json.config));
            this.setParent(region);

            return this;
        }

        public build(): ImageComponent {
            return new ImageComponent(this);
        }
    }
}