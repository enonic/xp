module api.content.page.region {

    import PropertyTree = api.data.PropertyTree;
    import PropertyEvent = api.data.PropertyEvent;

    export class DescriptorBasedComponent extends Component implements api.Equitable, api.Cloneable {

        public debug: boolean = false;

        public static PROPERTY_DESCRIPTOR = 'descriptor';

        public static PROPERTY_CONFIG = 'config';

        private disableEventForwarding: boolean;

        private descriptor: DescriptorKey;

        private config: PropertyTree;

        constructor(builder: DescriptorBasedComponentBuilder<any>) {

            super(builder);

            this.descriptor = builder.descriptor;
            this.config = builder.config;

            this.config.onChanged((event: PropertyEvent) => {
                if (this.debug) {
                    console.debug("DescriptorBasedComponent[" + this.getPath().toString() + "].config.onChanged: ", event);
                }
                if (!this.disableEventForwarding) {
                    this.notifyPropertyValueChanged(DescriptorBasedComponent.PROPERTY_CONFIG);
                }
            });
        }

        setDisableEventForwarding(value: boolean) {
            this.disableEventForwarding = value;
        }

        hasDescriptor(): boolean {
            if (this.descriptor) {
                return true;
            }
            else {
                return false;
            }
        }

        getDescriptor(): DescriptorKey {
            return this.descriptor;
        }

        setDescriptor(descriptorKey: DescriptorKey, descriptor?: Descriptor) {

            var oldValue = this.descriptor;
            this.descriptor = descriptorKey;

            this.alignNameWithDescriptor(descriptor);

            if (!api.ObjectHelper.equals(oldValue, descriptorKey)) {
                this.notifyPropertyChanged(DescriptorBasedComponent.PROPERTY_DESCRIPTOR);
            }
        }

        alignNameWithDescriptor(descriptor: Descriptor) {
            var newName = new ComponentName(!!descriptor ? descriptor.getDisplayName() : "");
            this.setName(newName);
        }

        getConfig(): PropertyTree {
            return this.config;
        }

        reset() {
            this.setDescriptor(null, null);
        }

        toComponentJson(): DescriptorBasedComponentJson {

            return <DescriptorBasedComponentJson>{
                "name": this.getName().toString(),
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