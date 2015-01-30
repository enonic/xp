module api.content.page.region {

    import PropertyTree = api.data.PropertyTree;
    import PropertyEvent = api.data.PropertyEvent;

    export class DescriptorBasedComponent extends Component implements api.Equitable, api.Cloneable {

        public static debug: boolean = false;

        public static PROPERTY_DESCRIPTOR = 'descriptor';

        public static PROPERTY_CONFIG = 'config';

        private disableEventForwarding: boolean;

        private descriptor: DescriptorKey;

        private config: PropertyTree;

        private configChangedHandler: (event: PropertyEvent) => void;

        constructor(builder: DescriptorBasedComponentBuilder<any>) {

            super(builder);

            this.descriptor = builder.descriptor;
            this.config = builder.config;

            this.configChangedHandler = (event: PropertyEvent) => {
                if (DescriptorBasedComponent.debug) {
                    console.debug("DescriptorBasedComponent[" + this.getPath().toString() + "].config.onChanged: ", event);
                }
                if (!this.disableEventForwarding) {
                    this.notifyPropertyValueChanged(DescriptorBasedComponent.PROPERTY_CONFIG);
                }
            };

            this.config.onChanged(this.configChangedHandler);
        }

        setDisableEventForwarding(value: boolean) {
            this.disableEventForwarding = value;
        }

        hasDescriptor(): boolean {
            return !!this.descriptor;
        }

        getDescriptor(): DescriptorKey {
            return this.descriptor;
        }

        setDescriptor(descriptorKey: DescriptorKey, descriptor: Descriptor) {

            var oldValue = this.descriptor;
            this.descriptor = descriptorKey;

            this.alignNameWithDescriptor(descriptor);

            if (!api.ObjectHelper.equals(oldValue, descriptorKey)) {
                this.notifyPropertyChanged(DescriptorBasedComponent.PROPERTY_DESCRIPTOR);
            }

            this.setConfig(new PropertyTree(this.config.getIdProvider()));
        }

        setConfig(config: PropertyTree) {
            var oldValue = this.config;
            if (oldValue) {
                this.config.unChanged(this.configChangedHandler);
            }
            this.config = config;
            this.config.onChanged(this.configChangedHandler);

            if (!api.ObjectHelper.equals(oldValue, config)) {
                this.notifyPropertyChanged(DescriptorBasedComponent.PROPERTY_CONFIG);
            }
        }

        alignNameWithDescriptor(descriptor: Descriptor) {
            var newName: ComponentName = descriptor ? new ComponentName(descriptor.getDisplayName()) : null;
            this.setName(newName);
        }

        getConfig(): PropertyTree {
            return this.config;
        }

        doReset() {
            this.setDescriptor(null, null);
        }

        toComponentJson(): DescriptorBasedComponentJson {

            return <DescriptorBasedComponentJson>{
                "name": this.getName() ? this.getName().toString() : null,
                "descriptor": this.descriptor != null ? this.descriptor.toString() : null,
                "config": this.config != null ? this.config.toJson() : null
            };
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, DescriptorBasedComponent)) {
                return false;
            }

            if (!super.equals(o)) {
                return false;
            }
            var other = <DescriptorBasedComponent>o;

            if (!api.ObjectHelper.equals(this.descriptor, other.descriptor)) {
                return false;
            }

            if (!api.ObjectHelper.equals(this.config, other.config)) {
                return false;
            }

            return true;
        }

        clone(generateNewPropertyIds: boolean = false): DescriptorBasedComponent {
            throw new Error("Must be implemented by inheritors");
        }
    }

    export class DescriptorBasedComponentBuilder<DESCRIPTOR_BASED_COMPONENT extends DescriptorBasedComponent> extends ComponentBuilder<DESCRIPTOR_BASED_COMPONENT> {

        descriptor: DescriptorKey;

        config: PropertyTree;

        constructor(source?: DescriptorBasedComponent, generateNewPropertyIds: boolean = false) {
            super(source);
            if (source) {
                this.descriptor = source.getDescriptor();
                this.config = source.getConfig() ? source.getConfig().copy(generateNewPropertyIds) : null;
            }
            else {
                this.config = new PropertyTree(api.Client.get().getPropertyIdProvider());
            }
        }

        public setDescriptor(value: DescriptorKey): ComponentBuilder<DESCRIPTOR_BASED_COMPONENT> {
            this.descriptor = value;
            return this;
        }

        public setConfig(value: PropertyTree): ComponentBuilder<DESCRIPTOR_BASED_COMPONENT> {
            this.config = value;
            return this;
        }


        public build(): DESCRIPTOR_BASED_COMPONENT {
            throw new Error("Must be implemented by inheritor");
        }
    }
}