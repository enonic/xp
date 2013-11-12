module api_content_page{

    export class PartTemplate extends Template<PartTemplateName> {

        constructor(builder:PartTemplateBuilder) {
            super(builder);
        }
    }

    export class PartTemplateBuilder extends TemplateBuilder<PartTemplateName> {

        public build():Template<PartTemplateName> {
            return new PartTemplate(this);
        }
    }
}