module api.content.page.layout {

    export class LayoutComponent extends api.content.page.PageComponent<LayoutTemplateKey> {

        constructor(builder: LayoutComponentBuilder) {
            super(builder);
        }

        toJson(): json.LayoutComponentJson {
            var json: json.LayoutComponentJson = <json.LayoutComponentJson>super.toJson();
            return json;
        }
    }

    export class LayoutComponentBuilder extends api.content.page.PageComponentBuilder<LayoutTemplateKey> {

        public fromJson(json: json.LayoutComponentJson): LayoutComponentBuilder {
            this.setTemplate(LayoutTemplateKey.fromString(json.template));
            this.setName(new api.content.page.ComponentName(json.name));
            this.setConfig(api.data.DataFactory.createRootDataSet(json.config));
            return this;
        }

        public build(): LayoutComponent {
            return new LayoutComponent(this);
        }
    }
}