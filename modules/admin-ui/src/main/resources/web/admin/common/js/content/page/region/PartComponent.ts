module api.content.page.region {

    import PropertyTree = api.data.PropertyTree;
    import PropertyIdProvider = api.data.PropertyIdProvider;

    export class PartComponent extends DescriptorBasedComponent implements api.Equitable, api.Cloneable {

        constructor(builder: PartComponentBuilder) {
            super(builder);
        }

        toJson(): ComponentTypeWrapperJson {
            var json: PartComponentJson = <PartComponentJson>super.toComponentJson();

            return <ComponentTypeWrapperJson> {
                PartComponent: json
            };
        }

        isEmpty(): boolean {
            return !this.hasDescriptor();
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, PartComponent)) {
                return false;
            }

            return super.equals(o);
        }

        clone(generateNewPropertyIds: boolean = false): PartComponent {
            return new PartComponentBuilder(this, generateNewPropertyIds).build();
        }
    }

    export class PartComponentBuilder extends DescriptorBasedComponentBuilder<PartComponent> {

        constructor(source?: PartComponent, generateNewPropertyIds: boolean = false) {

            super(source, generateNewPropertyIds);
        }

        public fromJson(json: PartComponentJson, region: Region, propertyIdProvider: PropertyIdProvider): PartComponentBuilder {

            if (json.descriptor) {
                this.setDescriptor(api.content.page.DescriptorKey.fromString(json.descriptor));
            }
            this.setName(json.name ? new ComponentName(json.name) : null);
            if (json.config) {
                this.setConfig(PropertyTree.fromJson(json.config, propertyIdProvider));
            }
            this.setParent(region);
            return this;
        }

        public build(): PartComponent {
            return new PartComponent(this);
        }
    }
}