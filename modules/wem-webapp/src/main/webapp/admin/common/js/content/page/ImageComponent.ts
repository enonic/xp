module api_content_page{

    export class ImageComponent extends PageComponent<ImageTemplateName> {

        private config:api_data.RootDataSet;

        private contentId:string;

        constructor(builder?:ImageComponentBuilder) {
            super(builder);
        }

        getConfig():api_data.RootDataSet {
            return this.config;
        }

        setConfig(value:api_data.RootDataSet) {
            this.config = value;
        }
    }

    export class ImageComponentBuilder extends ComponentBuilder<ImageTemplateName>{

        config:api_data.RootDataSet;

        public setConfig(value:api_data.RootDataSet):ImageComponentBuilder {
            this.config = value;
            return this;
        }

        public build():ImageComponent {
            return new ImageComponent(this);
        }
    }
}