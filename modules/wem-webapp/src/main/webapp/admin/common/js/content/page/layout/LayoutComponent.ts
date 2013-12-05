module api_content_page_layout {

    export class LayoutComponent extends api_content_page.BasePageComponent<LayoutTemplateKey> {

        private config: api_data.RootDataSet;

        constructor(builder: LayoutComponentBuilder) {
            super(builder);
            this.config = builder.config;
        }

        getConfig(): api_data.RootDataSet {
            return this.config;
        }
    }

    export class LayoutComponentBuilder extends api_content_page.BaseComponentBuilder<LayoutTemplateKey> {

        config: api_data.RootDataSet;

        public fromDataSet(dataSet: api_data.DataSet): LayoutComponentBuilder {
            this.setTemplate(LayoutTemplateKey.fromString(dataSet.getProperty("template").getString()));
            this.config = dataSet.getProperty("config").getValue().asRootDataSet();
            return this;
        }

        public setConfig(value: api_data.RootDataSet): LayoutComponentBuilder {
            this.config = value;
            return this;
        }

        public build(): LayoutComponent {
            return new LayoutComponent(this);
        }
    }
}