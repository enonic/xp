module api_content_page{

    export class ImageTemplate extends Template<ImageTemplateName> {

        constructor(builder:ImageTemplateBuilder) {
            super(builder);
        }
    }

    export class ImageTemplateBuilder extends TemplateBuilder<ImageTemplateName> {

        public build():Template<ImageTemplateName> {
            return new ImageTemplate(this);
        }
    }
}