module api.content.page.part {

    export class PartComponent extends api.content.page.PageComponent<PartTemplateKey> {

        private name: api.content.page.ComponentName;

        private config: api.data.RootDataSet;

        constructor(builder: PartComponentBuilder) {
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

    export class PartComponentBuilder extends api.content.page.ComponentBuilder<PartTemplateKey> {

        name: api.content.page.ComponentName;

        config: api.data.RootDataSet;

        public fromJson(json: json.PartComponentJson): PartComponentBuilder {

            this.setTemplate(PartTemplateKey.fromString(json.template));
            this.setName(new api.content.page.ComponentName(json.name));
            this.setConfig(api.data.DataFactory.createRootDataSet(json.config));
            return this;
        }

        public setName(value: api.content.page.ComponentName): PartComponentBuilder {
            this.name = value;
            return this;
        }

        public setConfig(value: api.data.RootDataSet): PartComponentBuilder {
            this.config = value;
            return this;
        }

        public build(): PartComponent {
            return new PartComponent(this);
        }
    }
}