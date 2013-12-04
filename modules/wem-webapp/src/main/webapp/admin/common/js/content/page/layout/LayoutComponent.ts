module api_content_page_layout{

    export class LayoutComponent extends api_content_page.BasePageComponent<LayoutTemplateKey,LayoutTemplateName> {

        private config:api_data.RootDataSet;

        constructor(builder:LayoutComponentBuilder) {
            super(builder);
        }

        getConfig():api_data.RootDataSet {
            return this.config;
        }
    }

    export class LayoutComponentBuilder extends api_content_page.BaseComponentBuilder<LayoutTemplateKey,LayoutTemplateName>{

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