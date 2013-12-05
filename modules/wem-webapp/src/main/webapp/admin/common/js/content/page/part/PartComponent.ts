module api_content_page_part{

    export class PartComponent extends api_content_page.BasePageComponent<PartTemplateKey> {

        private config:api_data.RootDataSet;

        constructor(builder:PartComponentBuilder) {
            super(builder);
            this.config = builder.config;
        }

        getConfig():api_data.RootDataSet {
            return this.config;
        }
    }

    export class PartComponentBuilder extends api_content_page.BaseComponentBuilder<PartTemplateKey>{

        config:api_data.RootDataSet;

        public fromDataSet(dataSet:api_data.DataSet):PartComponentBuilder {
            this.setTemplate( PartTemplateKey.fromString(dataSet.getProperty("template").getString()) );
            this.config = dataSet.getProperty("config").getValue().asRootDataSet();
            return this;
        }

        public setConfig(value:api_data.RootDataSet):PartComponentBuilder {
            this.config = value;
            return this;
        }

        public build():PartComponent {
            return new PartComponent(this);
        }
    }
}