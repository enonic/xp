module api.content.page.layout {

    export class LayoutComponent extends api.content.page.PageComponent<LayoutTemplateKey> {

        private name: api.content.page.ComponentName;

        private config: api.data.RootDataSet;

        constructor(builder: LayoutComponentBuilder) {
            super(builder);
            this.name = builder.name;
            this.config = builder.config;
        }

        getName(): api.content.page.ComponentName {
            return this.name;
        }

        getConfig(): api.data.RootDataSet {
            return this.config;
        }
    }

    export class LayoutComponentBuilder extends api.content.page.ComponentBuilder<LayoutTemplateKey> {

        name: api.content.page.ComponentName;

        config: api.data.RootDataSet;

        public fromJson(json: json.LayoutComponentJson): LayoutComponentBuilder {
            this.setTemplate(LayoutTemplateKey.fromString(json.template));
            this.setName(new api.content.page.ComponentName(json.name));
            this.setConfig(api.data.DataFactory.createRootDataSet(json.config));
            return this;
        }

        public setName(value: api.content.page.ComponentName): LayoutComponentBuilder {
            this.name = value;
            return this;
        }

        public setConfig(value: api.data.RootDataSet): LayoutComponentBuilder {
            this.config = value;
            return this;
        }

        public build(): LayoutComponent {
            return new LayoutComponent(this);
        }
    }
}