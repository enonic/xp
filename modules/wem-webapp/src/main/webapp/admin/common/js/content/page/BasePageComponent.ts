module api_content_page{

    export class BasePageComponent<TEMPLATE_KEY extends TemplateKey,TEMPLATE_NAME extends TemplateName> {

        private id:number;

        private template:Template<TEMPLATE_KEY,TEMPLATE_NAME>;

        constructor(builder?:BaseComponentBuilder<TEMPLATE_KEY,TEMPLATE_NAME>) {
            if( builder != undefined ) {
                this.template = builder.template;
            }
        }

        getTemplate():Template<TEMPLATE_KEY,TEMPLATE_NAME> {
            return this.template;
        }

        setTemplate(template:Template<TEMPLATE_KEY,TEMPLATE_NAME>) {
            this.template = template;
        }
    }

    export class BaseComponentBuilder<TEMPLATE_KEY extends TemplateKey,TEMPLATE_NAME extends TemplateName> {

        template:Template<TEMPLATE_KEY,TEMPLATE_NAME>;

        setTemplate(value:Template<TEMPLATE_KEY,TEMPLATE_NAME>):BaseComponentBuilder<TEMPLATE_KEY,TEMPLATE_NAME> {
            this.template = value;
            return this;
        }
    }
}