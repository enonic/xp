module api.content.page.part{

    export class PartComponent extends api.content.page.PageComponent<PartTemplateKey> {

        private config:api.data.RootDataSet;

        constructor(builder:PartComponentBuilder) {
            super(builder);
            this.config = builder.config;
        }

        getConfig():api.data.RootDataSet {
            return this.config;
        }
    }

    export class PartComponentBuilder extends api.content.page.BaseComponentBuilder<PartTemplateKey>{

        config:api.data.RootDataSet;

        public fromDataSet(dataSet:api.data.DataSet):PartComponentBuilder {
            this.setTemplate( PartTemplateKey.fromString(dataSet.getProperty("template").getString()) );
            this.config = dataSet.getProperty("config").getValue().asRootDataSet();
            return this;
        }

        public setConfig(value:api.data.RootDataSet):PartComponentBuilder {
            this.config = value;
            return this;
        }

        public build():PartComponent {
            return new PartComponent(this);
        }
    }
}