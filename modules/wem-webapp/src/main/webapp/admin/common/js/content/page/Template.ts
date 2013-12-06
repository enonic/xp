module api_content_page{

    export class Template<KEY extends TemplateKey,NAME extends TemplateName>
            extends TemplateSummary<KEY,NAME> {

        private config:api_data.RootDataSet;

        constructor(builder:TemplateBuilder<KEY,NAME>) {
            super(builder);
            this.config = builder.config;
        }

        getConfig():api_data.RootDataSet {
            return this.config;
        }
    }

    export class TemplateBuilder<KEY extends TemplateKey,NAME extends TemplateName> extends TemplateSummaryBuilder<KEY,NAME>{

        config:api_data.RootDataSet;

        public setConfig(value:api_data.RootDataSet):TemplateBuilder<KEY,NAME> {
            this.config = value;
            return this;
        }
    }
}