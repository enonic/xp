module api_content_page{

    export class Part extends PageComponent<PartTemplateName>{

        private config:api_data.RootDataSet;

        constructor(builder:PartBuilder) {
            super(builder);
        }

        getConfig():api_data.RootDataSet {
            return this.config;
        }
    }

    export class PartBuilder extends ComponentBuilder<PartTemplateName>{

        config:api_data.RootDataSet;

        public setConfig(value:api_data.RootDataSet):PartBuilder {
            this.config = value;
            return this;
        }

        public build():Part {
            return new Part(this);
        }
    }
}