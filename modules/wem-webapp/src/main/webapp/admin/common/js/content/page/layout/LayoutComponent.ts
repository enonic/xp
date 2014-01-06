module api.content.page.layout {

    export class LayoutComponent extends api.content.page.PageComponent<LayoutTemplateKey> {

        private config: api.data.RootDataSet;

        constructor(builder: LayoutComponentBuilder) {
            super(builder);
            this.config = builder.config;
        }

        getConfig(): api.data.RootDataSet {
            return this.config;
        }
    }

    export class LayoutComponentBuilder extends api.content.page.BaseComponentBuilder<LayoutTemplateKey> {

        config: api.data.RootDataSet;

        public fromDataSet(dataSet: api.data.DataSet): LayoutComponentBuilder {
            this.setTemplate(LayoutTemplateKey.fromString(dataSet.getProperty("template").getString()));
            this.config = dataSet.getProperty("config").getValue().asRootDataSet();
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