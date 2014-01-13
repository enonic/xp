module api.content.page.part {

    export class PartComponent extends api.content.page.PageComponent<PartTemplateKey> {

        private config: api.data.RootDataSet;

        constructor(builder: PartComponentBuilder) {
            super(builder);
            this.config = builder.config;
        }

        getConfig(): api.data.RootDataSet {
            return this.config;
        }
    }

    export class PartComponentBuilder extends api.content.page.ComponentBuilder<PartTemplateKey> {

        config: api.data.RootDataSet;

        public fromJson(json: json.PartComponentJson): PartComponentBuilder {

            this.setTemplate(PartTemplateKey.fromString(json.template));
            this.setName(new api.content.page.ComponentName(json.name));
            this.setConfig(api.data.DataFactory.createRootDataSet(json.config));
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