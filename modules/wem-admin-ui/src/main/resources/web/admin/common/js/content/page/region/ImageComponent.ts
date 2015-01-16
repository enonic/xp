module api.content.page.region {

    import Form = api.form.Form;
    import FormBuilder = api.form.FormBuilder;
    import OccurrencesBuilder = api.form.OccurrencesBuilder;
    import TextLine = api.form.inputtype.text.TextLine;
    import TextArea = api.form.inputtype.text.TextArea;
    import PropertyTree = api.data.PropertyTree;
    import PropertyIdProvider = api.data.PropertyIdProvider;

    export class ImageComponent extends Component implements api.Equitable, api.Cloneable {

        public debug: boolean = false;

        private disableEventForwarding: boolean;

        private image: api.content.ContentId;

        private config: PropertyTree;

        private form: Form;

        constructor(builder: ImageComponentBuilder) {
            super(builder);

            this.image = builder.image;
            this.config = builder.config;
            this.config.onChanged(() => {
                if (this.debug) {
                    console.debug("ImageComponent[" + this.getPath().toString() + "].config.onChanged: ", event);
                }
                if (!this.disableEventForwarding) {
                    this.notifyPropertyValueChanged("config");
                }
            });

            var formBuilder = new FormBuilder();
            formBuilder.addFormItem(new api.form.InputBuilder().
                setName("caption").
                setInputType(TextArea.getName()).
                setLabel("Caption").
                setOccurrences(new OccurrencesBuilder().setMinimum(0).setMaximum(1).build()).
                build());
            this.form = formBuilder.build();
        }

        setDisableEventForwarding(value: boolean) {
            this.disableEventForwarding = value;
        }

        getImage(): api.content.ContentId {
            return this.image;
        }

        getForm(): api.form.Form {
            return this.form;
        }

        getConfig(): PropertyTree {
            return this.config;
        }

        setImage(value: api.content.ContentId) {
            var oldValue = this.image;
            this.image = value;
            if (!api.ObjectHelper.equals(oldValue, value)) {
                this.notifyPropertyChanged("image");
            }
        }

        reset() {
            this.setImage(null);
        }

        isEmpty(): boolean {
            return !this.image;
        }

        toJson(): ComponentTypeWrapperJson {

            var json: ImageComponentJson = <ImageComponentJson>super.toComponentJson();
            json.image = this.image != null ? this.image.toString() : null;
            json.config = this.config != null ? this.config.toJson() : null;

            return <ComponentTypeWrapperJson> {
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

        clone(generateNewPropertyIds: boolean = false): ImageComponent {
            return new ImageComponentBuilder(this, generateNewPropertyIds).build();
        }
    }

    export class ImageComponentBuilder extends ComponentBuilder<ImageComponent> {

        image: api.content.ContentId;

        config: PropertyTree;

        constructor(source?: ImageComponent, generateNewPropertyIds: boolean = false) {
            super(source);
            if (source) {
                this.image = source.getImage();
                this.config = source.getConfig() ? source.getConfig().copy(generateNewPropertyIds) : null;
            }
            else {
                this.config = new PropertyTree(api.Client.get().getPropertyIdProvider());
            }
        }

        public setImage(value: api.content.ContentId): ImageComponentBuilder {
            this.image = value;
            return this;
        }

        public setConfig(value: PropertyTree): ImageComponentBuilder {
            this.config = value;
            return this;
        }

        public fromJson(json: ImageComponentJson, region: Region, propertyIdProvider: PropertyIdProvider): ImageComponentBuilder {

            if (json.image) {
                this.setImage(new api.content.ContentId(json.image));
            }

            this.setName(new ComponentName(json.name));


            if (json.config) {
                this.setConfig(PropertyTree.fromJson(json.config, propertyIdProvider));
            }

            this.setParent(region);

            return this;
        }

        public build(): ImageComponent {
            return new ImageComponent(this);
        }
    }
}