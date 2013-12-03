module api_content_page{

    export class LayoutComponent extends PageComponent<LayoutTemplateName> {

        private config:api_data.RootDataSet;

        constructor(builder:LayoutComponentBuilder) {
            super(builder);
        }

        getConfig():api_data.RootDataSet {
            return this.config;
        }
    }

    export class LayoutComponentBuilder extends ComponentBuilder<LayoutTemplateName>{

        config:api_data.RootDataSet;

        public setConfig(value:api_data.RootDataSet):LayoutComponentBuilder {
            this.config = value;
            return this;
        }

        public build():LayoutComponent {
            return new LayoutComponent(this);
        }
    }
}