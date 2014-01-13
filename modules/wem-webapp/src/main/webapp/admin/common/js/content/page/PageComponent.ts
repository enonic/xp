module api.content.page {

    export class PageComponent<TEMPLATE_KEY extends TemplateKey> {

        private name: api.content.page.ComponentName;

        private template: TEMPLATE_KEY;

        constructor(builder?: ComponentBuilder<TEMPLATE_KEY>) {
            if (builder != undefined) {
                this.name = builder.name;
                this.template = builder.template;
            }
        }

        getName(): api.content.page.ComponentName {
            return this.name;
        }

        getTemplate(): TEMPLATE_KEY {
            return this.template;
        }

        setTemplate(template: TEMPLATE_KEY) {
            this.template = template;
        }
    }

    export class ComponentBuilder<TEMPLATE_KEY extends TemplateKey> {

        name: api.content.page.ComponentName;

        template: TEMPLATE_KEY;

        public setName(value: api.content.page.ComponentName): ComponentBuilder<TEMPLATE_KEY> {
            this.name = value;
            return this;
        }

        public setTemplate(value: TEMPLATE_KEY): ComponentBuilder<TEMPLATE_KEY> {
            this.template = value;
            return this;
        }
    }
}