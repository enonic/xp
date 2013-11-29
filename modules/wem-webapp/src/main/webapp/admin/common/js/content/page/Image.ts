module api_content_page{

    export class Image extends PageComponent<ImageTemplateName> {

        private config:api_data.RootDataSet;

        constructor(builder?:ImageBuilder) {
            super(builder);
        }

        getConfig():api_data.RootDataSet {
            return this.config;
        }

        setConfig(value:api_data.RootDataSet) {
            this.config = value;
        }
    }

    export class ImageBuilder extends ComponentBuilder<ImageTemplateName>{

        config:api_data.RootDataSet;

        public setConfig(value:api_data.RootDataSet):ImageBuilder {
            this.config = value;
            return this;
        }

        public build():Image {
            return new Image(this);
        }
    }
}