module api.content.page.part {

    export class PartComponent extends api.content.page.PageComponent {

        constructor(builder: PartComponentBuilder) {
            super(builder);
        }

        toJson(): api.content.page.json.PageComponentTypeWrapperJson {
            var json: json.PartComponentJson = <json.PartComponentJson>super.toPageComponentJson();

            return <api.content.page.json.PageComponentTypeWrapperJson> {
                PartComponent: json
            };
        }
    }

    export class PartComponentBuilder extends api.content.page.PageComponentBuilder<PartComponent> {

        public fromJson(json: json.PartComponentJson): PartComponentBuilder {

            if (json.template) {
                this.setTemplate(api.content.page.TemplateKey.fromString(json.template));
            }
            this.setName(new api.content.page.ComponentName(json.name));
            this.setConfig(api.data.DataFactory.createRootDataSet(json.config));
            return this;
        }

        public build(): PartComponent {
            return new PartComponent(this);
        }
    }
}