module api_content_page_image{

    export class ImageComponent extends api_content_page.PageComponent<ImageTemplateName> {

        private config:api_data.RootDataSet;

        private imageContent:api_content.ContentId;

        constructor(builder?:ImageComponentBuilder) {
            super(builder);
        }

        getConfig():api_data.RootDataSet {
            return this.config;
        }

        setConfig(value:api_data.RootDataSet) {
            this.config = value;
        }

        setImageContent(value:api_content.ContentId) {
            this.imageContent = value;
        }
    }

    export class ImageComponentBuilder extends api_content_page.ComponentBuilder<ImageTemplateName>{

        config:api_data.RootDataSet;

        imageContent:api_content.ContentId;

        public setConfig(value:api_data.RootDataSet):ImageComponentBuilder {
            this.config = value;
            return this;
        }

        public setImageContent(value:api_content.ContentId):ImageComponentBuilder {
            this.imageContent = value;
            return this;
        }

        public build():ImageComponent {
            return new ImageComponent(this);
        }
    }
}