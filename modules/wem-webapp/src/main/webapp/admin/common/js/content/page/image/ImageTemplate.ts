module api_content_page_image{

    export class ImageTemplate extends api_content_page.Template<ImageTemplateName> {

        constructor(builder:ImageTemplateBuilder) {
            super(builder);
        }
    }

    export class ImageTemplateBuilder extends api_content_page.TemplateBuilder<ImageTemplateName> {

        public build():api_content_page.Template<ImageTemplateName> {
            return new ImageTemplate(this);
        }
    }
}