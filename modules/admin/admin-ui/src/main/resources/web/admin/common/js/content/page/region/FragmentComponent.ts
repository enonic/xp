module api.content.page.region {

    import PropertyTree = api.data.PropertyTree;
    import PropertyEvent = api.data.PropertyEvent;

    export class FragmentComponent extends Component implements api.Equitable, api.Cloneable {

        public static PROPERTY_FRAGMENT = 'fragment';

        public static PROPERTY_CONFIG = 'config';

        public static debug: boolean = false;

        private disableEventForwarding: boolean;

        private fragment: api.content.ContentId;

        private config: PropertyTree;

        private configChangedHandler: (event: PropertyEvent) => void;

        constructor(builder: FragmentComponentBuilder) {
            super(builder);

            this.fragment = builder.fragment;
            this.config = builder.config;
            this.configChangedHandler = (event: PropertyEvent) => {
                if (FragmentComponent.debug) {
                    console.debug("FragmentComponent[" + this.getPath().toString() + "].config.onChanged: ", event);
                }
                if (!this.disableEventForwarding) {
                    this.notifyPropertyValueChanged(FragmentComponent.PROPERTY_CONFIG);
                }
            };

            this.config.onChanged(this.configChangedHandler);
        }

        setDisableEventForwarding(value: boolean) {
            this.disableEventForwarding = value;
        }

        getFragment(): api.content.ContentId {
            return this.fragment;
        }

        getConfig(): PropertyTree {
            return this.config;
        }

        setFragment(contentId: api.content.ContentId, name: string) {
            var oldValue = this.fragment;
            this.fragment = contentId;

            this.setName(name ? new ComponentName(name) : this.getType().getDefaultName());

            if (!api.ObjectHelper.equals(oldValue, contentId)) {
                this.notifyPropertyChanged(FragmentComponent.PROPERTY_FRAGMENT);
            }
        }

        hasFragment(): boolean {
            return !!this.fragment;
        }

        doReset() {
            this.setFragment(null, null);
        }

        isEmpty(): boolean {
            return !this.fragment;
        }

        toJson(): ComponentTypeWrapperJson {

            var json: FragmentComponentJson = <FragmentComponentJson>super.toComponentJson();
            json.fragment = this.fragment != null ? this.fragment.toString() : null;
            json.config = this.config != null ? this.config.toJson() : null;

            return <ComponentTypeWrapperJson> {
                FragmentComponent: json
            };
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, FragmentComponent)) {
                return false;
            }

            var other = <FragmentComponent>o;

            if (!super.equals(o)) {
                return false;
            }

            if (!api.ObjectHelper.equals(this.fragment, other.fragment)) {
                return false;
            }

            if (!api.ObjectHelper.equals(this.config, other.config)) {
                return false;
            }

            return true;
        }

        clone(): FragmentComponent {
            return new FragmentComponentBuilder(this).build();
        }
    }

    export class FragmentComponentBuilder extends ComponentBuilder<FragmentComponent> {

        fragment: api.content.ContentId;

        config: PropertyTree;

        constructor(source?: FragmentComponent) {
            super(source);
            if (source) {
                this.fragment = source.getFragment();
                this.config = source.getConfig() ? source.getConfig().copy() : null;
            } else {
                this.config = new PropertyTree();
            }
            this.setType(FragmentComponentType.get());
        }

        public setFragment(value: api.content.ContentId): FragmentComponentBuilder {
            this.fragment = value;
            return this;
        }

        public setConfig(value: PropertyTree): FragmentComponentBuilder {
            this.config = value;
            return this;
        }

        public fromJson(json: FragmentComponentJson, region: Region): FragmentComponentBuilder {

            if (json.fragment) {
                this.setFragment(new api.content.ContentId(json.fragment));
            }

            this.setName(json.name ? new ComponentName(json.name) : null);


            if (json.config) {
                this.setConfig(PropertyTree.fromJson(json.config));
            }

            this.setParent(region);

            return this;
        }

        public build(): FragmentComponent {
            return new FragmentComponent(this);
        }
    }
}