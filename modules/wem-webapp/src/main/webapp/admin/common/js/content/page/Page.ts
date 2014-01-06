module api.content.page{

    export class Page extends PageComponent<PageTemplateKey> {

        private config:api.data.RootDataSet;

        constructor(builder:PageBuilder) {
            super(builder);
            this.config = builder.config;
        }

        hasConfig():boolean {
            return this.config != null;
        }

        getConfig():api.data.RootDataSet {
            return this.config;
        }
    }

    export class PageBuilder extends BaseComponentBuilder<PageTemplateKey>{

        config:api.data.RootDataSet;

        public fromJson(json:api.content.page.json.PageJson):PageBuilder {
            this.setTemplate(PageTemplateKey.fromString(json.template));
            this.setConfig(api.data.DataFactory.createRootDataSet(json.config));
            return this;
        }

        public setConfig(value:api.data.RootDataSet):PageBuilder {
            this.config = value;
            return this;
        }

        public build():Page {
            return new Page(this);
        }
    }
}