module api.content.page.part {

    import Region = api.content.page.region.Region;
    import PropertyTree = api.data.PropertyTree;
    import PropertyIdProvider = api.data.PropertyIdProvider;

    export class PartComponent extends api.content.page.DescriptorBasedComponent implements api.Equitable, api.Cloneable {

        constructor(builder: PartComponentBuilder) {
            super(builder);
        }

        toJson(): api.content.page.ComponentTypeWrapperJson {
            var json: PartComponentJson = <PartComponentJson>super.toPageComponentJson();

            return <api.content.page.ComponentTypeWrapperJson> {
                PartComponent: json
            };
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

    export class PartComponentBuilder extends api.content.page.DescriptorBasedPageComponentBuilder<PartComponent> {

        constructor(source?: PartComponent, generateNewPropertyIds: boolean = false) {

            super(source, generateNewPropertyIds);
        }

        public fromJson(json: PartComponentJson, region: Region, propertyIdProvider: PropertyIdProvider): PartComponentBuilder {

            if (json.descriptor) {
                this.setDescriptor(api.content.page.DescriptorKey.fromString(json.descriptor));
            }
            this.setName(new api.content.page.ComponentName(json.name));
            this.setConfig(PropertyTree.fromJson(json.config, propertyIdProvider));
            this.setParent(region);
            return this;
        }

        public build(): PartComponent {
            return new PartComponent(this);
        }
    }
}