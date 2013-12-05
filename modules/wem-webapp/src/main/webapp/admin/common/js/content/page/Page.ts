module api_content_page{

    export class Page extends BasePageComponent<PageTemplateKey> {

        private config:api_data.RootDataSet;

        constructor(builder:PageBuilder) {
            super(builder);
            this.config = builder.config;
        }

        hasConfig():boolean {
            return this.config != null;
        }

        getConfig():api_data.RootDataSet {
            return this.config;
        }
    }

    export class PageBuilder extends BaseComponentBuilder<PageTemplateKey>{

        config:api_data.RootDataSet;

        public fromJson(json:api_content_page_json.PageJson):PageBuilder {
            this.setTemplate(PageTemplateKey.fromString(json.template));
            this.setConfig(api_data.DataFactory.createRootDataSet(json.config));
            return this;
        }

        public setConfig(value:api_data.RootDataSet):PageBuilder {
            this.config = value;
            return this;
        }

        public build():Page {
            return new Page(this);
        }
    }
}