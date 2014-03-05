module api.content.page.text {

    export class TextComponent extends api.content.page.PageComponent {

        private text: string;

        constructor(builder?: TextComponentBuilder) {
            super(builder);
            if (builder) {
                this.text = builder.text;
            }
        }

        getText(): string {
            return this.text;
        }

        setText(value: string) {
            this.text = value;
        }

        toJson(): api.content.page.json.PageComponentTypeWrapperJson {

            var json: json.TextComponentJson = <json.TextComponentJson>super.toPageComponentJson();
            json.text = this.text;

            return <api.content.page.json.PageComponentTypeWrapperJson> {
                TextComponent: json
            };
        }
    }

    export class TextComponentBuilder extends api.content.page.PageComponentBuilder<TextComponent> {

        text: string;

        public fromJson(json: json.TextComponentJson, regionPath: RegionPath): TextComponentBuilder {

            this.text = json.text;

            this.setName(new api.content.page.ComponentName(json.name));

            if (json.descriptor) {
                this.setDescriptor(api.content.page.DescriptorKey.fromString(json.descriptor));
            }

            this.setConfig(api.data.DataFactory.createRootDataSet(json.config));
            this.setRegion(regionPath);

            return this;
        }

        public setText(value: string): TextComponentBuilder {
            this.text = value;
            return this;
        }

        public build(): TextComponent {
            return new TextComponent(this);
        }
    }
}