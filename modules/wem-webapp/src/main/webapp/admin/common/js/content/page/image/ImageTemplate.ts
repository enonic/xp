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

        static fromJson( json: api_content_page_json.ImageTemplateSummaryJson ):ImageTemplate {
            var imageTemplate: ImageTemplateBuilder = new ImageTemplateBuilder();
            imageTemplate.setName( new ImageTemplateName( json.name ) );
            imageTemplate.setDisplayName( json.displayName );
            return imageTemplate.build();
        }
    }
}