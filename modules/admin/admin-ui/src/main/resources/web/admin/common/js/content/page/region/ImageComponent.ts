module api.content.page.region {

    import Form = api.form.Form;
    import FormBuilder = api.form.FormBuilder;
    import OccurrencesBuilder = api.form.OccurrencesBuilder;
    import TextLine = api.form.inputtype.text.TextLine;
    import TextArea = api.form.inputtype.text.TextArea;
    import PropertyTree = api.data.PropertyTree;
    import PropertyEvent = api.data.PropertyEvent;

    export class ImageComponent extends Component implements api.Equitable, api.Cloneable {

        public static PROPERTY_IMAGE = 'image';

        public static PROPERTY_CONFIG = 'config';

        public static debug: boolean = false;

        private disableEventForwarding: boolean;

        private image: api.content.ContentId;

        private config: PropertyTree;

        private form: Form;

        private configChangedHandler: (event: PropertyEvent) => void;

        constructor(builder: ImageComponentBuilder) {
            super(builder);

            this.image = builder.image;
            this.config = builder.config;
            this.configChangedHandler = (event: PropertyEvent) => {
                if (ImageComponent.debug) {
                    console.debug("ImageComponent[" + this.getPath().toString() + "].config.onChanged: ", event);
                }
                if (!this.disableEventForwarding) {
                    this.notifyPropertyValueChanged(ImageComponent.PROPERTY_CONFIG);
                }
            };

            this.config.onChanged(this.configChangedHandler);

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

        setImage(contentId: api.content.ContentId, name: string) {
            var oldValue = this.image;
            this.image = contentId;

            this.setName(name ? new ComponentName(name) : null);

            if (!api.ObjectHelper.equals(oldValue, contentId)) {
                this.notifyPropertyChanged(ImageComponent.PROPERTY_IMAGE);
            }
        }

        hasImage(): boolean {
            return !!this.image;
        }

        doReset() {
            this.setImage(null, null);
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

        clone(): ImageComponent {
            return new ImageComponentBuilder(this).build();
        }
    }

    export class ImageComponentBuilder extends ComponentBuilder<ImageComponent> {

        image: api.content.ContentId;

        config: PropertyTree;

        constructor(source?: ImageComponent) {
            super(source);
            if (source) {
                this.image = source.getImage();
                this.config = source.getConfig() ? source.getConfig().copy() : null;
            }
            else {
                this.config = new PropertyTree();
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

        public fromJson(json: ImageComponentJson, region: Region): ImageComponentBuilder {

            if (json.image) {
                this.setImage(new api.content.ContentId(json.image));
            }

            this.setName(json.name ? new ComponentName(json.name) : null);


            if (json.config) {
                this.setConfig(PropertyTree.fromJson(json.config));
            }

            this.setParent(region);

            return this;
        }

        public build(): ImageComponent {
            return new ImageComponent(this);
        }
    }
}