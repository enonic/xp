module api.content.page {

    export class DescriptorBasedPageComponent extends PageComponent implements api.Equitable, api.Cloneable {

        private descriptorKey: DescriptorKey;

        private config: api.data.RootDataSet;

        constructor(builder?: DescriptorBasedPageComponentBuilder<any>) {

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

        setConfig(value: api.data.RootDataSet) {
            this.config = value;
        }

        getConfig(): api.data.RootDataSet {
            return this.config;
        }

        reset() {
            this.descriptorKey = null;
        }

        toPageComponentJson(): DescriptorBasedPageComponentJson {

            return {
                "name": this.getName().toString(),
                "descriptor": this.descriptorKey != null ? this.descriptorKey.toString() : null,
                "config": this.config != null ? this.config.toJson() : null
            };
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, DescriptorBasedPageComponent)) {
                return false;
            }

            if (!super.equals(o)) {
                return false;
            }
            var other = <DescriptorBasedPageComponent>o;

            if (!api.ObjectHelper.equals(this.descriptorKey, other.descriptorKey)) {
                return false;
            }

            if (!api.ObjectHelper.equals(this.config, other.config)) {
                return false;
            }

            return true;
        }

        clone(): PageComponent {
            throw new Error("Must be implemented by inheritors");
        }
    }

    export class DescriptorBasedPageComponentBuilder<DESCRIPTOR_BASED_COMPONENT extends DescriptorBasedPageComponent> extends PageComponentBuilder<DESCRIPTOR_BASED_COMPONENT> {

        descriptor: DescriptorKey;

        config: api.data.RootDataSet;

        constructor(source?: DescriptorBasedPageComponent) {
            super(source);
            if( source ) {
                this.descriptor = source.getDescriptor();
                this.config = source.getConfig().clone();
            }
        }

        public setDescriptor(value: DescriptorKey): PageComponentBuilder<DESCRIPTOR_BASED_COMPONENT> {
            this.descriptor = value;
            return this;
        }

        public setConfig(value: api.data.RootDataSet): PageComponentBuilder<DESCRIPTOR_BASED_COMPONENT> {
            this.config = value;
            return this;
        }


        public build(): DESCRIPTOR_BASED_COMPONENT {
            throw new Error("Must be implemented by inheritor");
        }
    }
}