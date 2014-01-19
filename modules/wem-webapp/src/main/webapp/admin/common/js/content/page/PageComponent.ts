module api.content.page {

    export class PageComponent<TEMPLATE_KEY extends TemplateKey> {

        private name: api.content.page.ComponentName;

        private template: TEMPLATE_KEY;

        private config: api.data.RootDataSet;

        constructor(builder?: PageComponentBuilder<TEMPLATE_KEY>) {
            if (builder != undefined) {
                this.name = builder.name;
                this.template = builder.template;
                this.config = builder.config;
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

        setConfig(value: api.data.RootDataSet) {
            this.config = value;
        }

        getConfig(): api.data.RootDataSet {
            return this.config;
        }

        toJson(): json.PageComponentJson {
            return {
                "type" : api.util.getClassName(this),
                "name": this.name.toString(),
                "template": this.template.toString(),
                "config": this.config.toJson()
            };
        }
    }

    export class PageComponentBuilder<TEMPLATE_KEY extends TemplateKey> {

        name: api.content.page.ComponentName;

        template: TEMPLATE_KEY;

        config: api.data.RootDataSet;

        public setName(value: api.content.page.ComponentName): PageComponentBuilder<TEMPLATE_KEY> {
            this.name = value;
            return this;
        }

        public setTemplate(value: TEMPLATE_KEY): PageComponentBuilder<TEMPLATE_KEY> {
            this.template = value;
            return this;
        }

        public setConfig(value: api.data.RootDataSet): PageComponentBuilder<TEMPLATE_KEY> {
            this.config = value;
            return this;
        }
    }
}