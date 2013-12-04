module api_content_page{

    export class Template<KEY extends TemplateKey,NAME extends TemplateName>
            extends TemplateSummary<KEY,NAME> {

        private descriptor:ComponentDescriptor;

        private config:api_data.RootDataSet;

        constructor(builder:TemplateBuilder<KEY,NAME>) {
            super(builder);
            this.descriptor = builder.descriptor;
            this.config = builder.config;
        }

        getDescriptor():ComponentDescriptor {
            return this.descriptor;
        }

        getConfig():api_data.RootDataSet {
            return this.config;
        }
    }

    export class TemplateBuilder<KEY extends TemplateKey,NAME extends TemplateName> extends TemplateSummaryBuilder<KEY,NAME>{

        descriptor:ComponentDescriptor;

        config:api_data.RootDataSet;

        public setDescriptor(value:ComponentDescriptor):TemplateBuilder<KEY,NAME> {
            this.descriptor = value;
            return this;
        }

        public setConfig(value:api_data.RootDataSet):TemplateBuilder<KEY,NAME> {
            this.config = value;
            return this;
        }
    }
}