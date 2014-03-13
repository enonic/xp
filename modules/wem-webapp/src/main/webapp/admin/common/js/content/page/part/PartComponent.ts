module api.content.page.part {

    export class PartComponent extends api.content.page.PageComponent {

        constructor(builder: PartComponentBuilder) {
            super(builder);
        }

        toJson(): api.content.page.PageComponentTypeWrapperJson {
            var json: PartComponentJson = <PartComponentJson>super.toPageComponentJson();

            return <api.content.page.PageComponentTypeWrapperJson> {
                PartComponent: json
            };
        }
    }

    export class PartComponentBuilder extends api.content.page.PageComponentBuilder<PartComponent> {

        public fromJson(json: PartComponentJson, regionPath: RegionPath): PartComponentBuilder {

            if (json.descriptor) {
                this.setDescriptor(api.content.page.DescriptorKey.fromString(json.descriptor));
            }
            this.setName(new api.content.page.ComponentName(json.name));
            this.setConfig(api.data.DataFactory.createRootDataSet(json.config));
            this.setRegion(regionPath);
            return this;
        }

        public build(): PartComponent {
            return new PartComponent(this);
        }
    }
}