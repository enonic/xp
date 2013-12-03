module api_content_page{

    export class PartComponent extends PageComponent<PartTemplateName>{

        private config:api_data.RootDataSet;

        constructor(builder:PartComponentBuilder) {
            super(builder);
        }

        getConfig():api_data.RootDataSet {
            return this.config;
        }
    }

    export class PartComponentBuilder extends ComponentBuilder<PartTemplateName>{

        config:api_data.RootDataSet;

        public setConfig(value:api_data.RootDataSet):PartComponentBuilder {
            this.config = value;
            return this;
        }

        public build():PartComponent {
            return new PartComponent(this);
        }
    }
}