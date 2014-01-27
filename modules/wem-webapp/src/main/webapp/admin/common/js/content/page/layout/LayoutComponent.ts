module api.content.page.layout {

    export class LayoutComponent extends api.content.page.PageComponent<LayoutTemplateKey> {

        private regions: LayoutRegions;

        constructor(builder: LayoutComponentBuilder) {
            super(builder);
            this.regions = builder.regions;
        }

        getLayoutRegions(): LayoutRegions {
            return this.regions;
        }

        toJson(): api.content.page.json.PageComponentTypeWrapperJson {
            var json: json.LayoutComponentJson = <json.LayoutComponentJson>super.toPageComponentJson();
            json.regions = this.regions.toJson();

            return <api.content.page.json.PageComponentTypeWrapperJson> {
                LayoutComponent: json
            };
        }
    }

    export class LayoutComponentBuilder extends api.content.page.PageComponentBuilder<LayoutTemplateKey,LayoutComponent> {

        regions: LayoutRegions;

        public fromJson(json: json.LayoutComponentJson): LayoutComponentBuilder {
            if( json.template ) {
                this.setTemplate(LayoutTemplateKey.fromString(json.template));
            }
            this.setName(new api.content.page.ComponentName(json.name));
            this.setConfig(api.data.DataFactory.createRootDataSet(json.config));
            this.setRegions(json.regions != null ? new LayoutRegionsBuilder().fromJson(json.regions).build() : null);
            return this;
        }

        public setRegions(value: LayoutRegions): LayoutComponentBuilder {
            this.regions = value;
            return this;
        }

        public build(): LayoutComponent {
            return new LayoutComponent(this);
        }
    }
}