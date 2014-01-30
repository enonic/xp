module api.content.page {

    export class Template extends TemplateSummary {

        private config: api.data.RootDataSet;

        constructor(builder: TemplateBuilder) {
            super(builder);
            this.config = builder.config;
        }

        getConfig(): api.data.RootDataSet {
            return this.config;
        }
    }

    export class TemplateBuilder extends TemplateSummaryBuilder {

        config: api.data.RootDataSet;

        public setConfig(value: api.data.RootDataSet): TemplateBuilder {
            this.config = value;
            return this;
        }
    }
}