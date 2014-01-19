module api.content.page.part {

    export class PartComponent extends api.content.page.PageComponent<PartTemplateKey> {

        constructor(builder: PartComponentBuilder) {
            super(builder);
        }

        toJson(): json.PartComponentJson {
            var json: json.PartComponentJson = <json.PartComponentJson>super.toJson();
            return json;
        }
    }

    export class PartComponentBuilder extends api.content.page.PageComponentBuilder<PartTemplateKey> {

        public fromJson(json: json.PartComponentJson): PartComponentBuilder {

            this.setTemplate(PartTemplateKey.fromString(json.template));
            this.setName(new api.content.page.ComponentName(json.name));
            this.setConfig(api.data.DataFactory.createRootDataSet(json.config));
            return this;
        }

        public build(): PartComponent {
            return new PartComponent(this);
        }
    }
}