module api_content_page{

    export class Template<NAME extends TemplateName> {

        private name:NAME;

        private displayName:string;

        private descriptor:ComponentDescriptor;

        private config:api_data.RootDataSet;

        constructor(builder:TemplateBuilder<NAME>) {
            this.name = builder.name;
            this.displayName = builder.displayName;
            this.descriptor = builder.descriptor;
            this.config = builder.config;
        }

        getName():NAME {
            return this.name;
        }

        getDisplayName():string {
            return this.displayName;
        }

        getDescriptor():ComponentDescriptor {
            return this.descriptor;
        }

        getConfig():api_data.RootDataSet {
            return this.config;
        }
    }

    export class TemplateBuilder<NAME extends TemplateName> {

        name:NAME;

        displayName:string;

        descriptor:ComponentDescriptor;

        config:api_data.RootDataSet;

        public setName(value:NAME):TemplateBuilder<NAME> {
            this.name = value;
            return this;
        }

        public setDisplayName(value:string):TemplateBuilder<NAME> {
            this.displayName = value;
            return this;
        }

        public setDescriptor(value:ComponentDescriptor):TemplateBuilder<NAME> {
            this.descriptor = value;
            return this;
        }

        public setConfig(value:api_data.RootDataSet):TemplateBuilder<NAME> {
            this.config = value;
            return this;
        }
    }
}