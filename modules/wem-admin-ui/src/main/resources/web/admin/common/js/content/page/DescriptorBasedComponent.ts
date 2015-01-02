module api.content.page {

    import PropertyTree = api.data.PropertyTree;

    export class DescriptorBasedComponent extends Component implements api.Equitable, api.Cloneable {

        private descriptorKey: DescriptorKey;

        private config: PropertyTree;

        constructor(builder?: DescriptorBasedComponentBuilder<any>) {

            super(builder);

            if (builder != undefined) {
                this.descriptorKey = builder.descriptor;
                this.config = builder.config;
            }
        }

        hasDescriptor(): boolean {
            if (this.descriptorKey) {
                return true;
            }
            else {
                return false;
            }
        }

        getDescriptor(): DescriptorKey {
            return this.descriptorKey;
        }

        setDescriptor(key: DescriptorKey) {
            this.descriptorKey = key;
        }

        getConfig(): PropertyTree {
            return this.config;
        }

        reset() {
            this.descriptorKey = null;
        }

        toComponentJson(): DescriptorBasedComponentJson {

            return {
                "name": this.getName().toString(),
                "descriptor": this.descriptorKey != null ? this.descriptorKey.toString() : null,
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

            if (!api.ObjectHelper.equals(this.descriptorKey, other.descriptorKey)) {
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
            if( source ) {
                this.descriptor = source.getDescriptor();
                this.config = source.getConfig().copy(generateNewPropertyIds);
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