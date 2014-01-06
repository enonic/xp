module api.content.page{

    export class PageComponent<TEMPLATE_KEY extends TemplateKey> {

        private template:TEMPLATE_KEY;

        constructor(builder?:BaseComponentBuilder<TEMPLATE_KEY>) {
            if( builder != undefined ) {
                this.template = builder.template;
            }
        }

        getTemplate():TEMPLATE_KEY {
            return this.template;
        }

        setTemplate(template:TEMPLATE_KEY) {
            this.template = template;
        }
    }

    export class BaseComponentBuilder<TEMPLATE_KEY extends TemplateKey> {

        template:TEMPLATE_KEY;

        setTemplate(value:TEMPLATE_KEY):BaseComponentBuilder<TEMPLATE_KEY> {
            this.template = value;
            return this;
        }
    }
}