module api.content.page.text {

    export class TextComponent extends api.content.page.PageComponent implements api.Equitable, api.Cloneable {

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

        toJson(): api.content.page.PageComponentTypeWrapperJson {

            var json: TextComponentJson = <TextComponentJson>super.toPageComponentJson();
            json.text = this.text != null ? this.text : null;

            return <api.content.page.PageComponentTypeWrapperJson> {
                TextComponent: json
            };
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, TextComponent)) {
                return false;
            }

            var other = <TextComponent>o;

            if (!super.equals(o)) {
                return false;
            }

            if (!api.ObjectHelper.stringEquals(this.text, other.text)) {
                return false;
            }

            return true;
        }

        clone(): TextComponent {
            return new TextComponentBuilder(this).build();
        }
    }

    export class TextComponentBuilder extends api.content.page.PageComponentBuilder<TextComponent> {

        text: string;

        constructor(source?: TextComponent) {

            super();

            if (source) {
                this.text = source.getText();

                this.name = source.getName();
                this.parent = source.getParent();
            }
        }

        public fromJson(json: TextComponentJson, regionPath: RegionPath): TextComponentBuilder {

            if (json.text) {
                this.setText(json.text);
            }

            this.setName(new api.content.page.ComponentName(json.name));
            this.setParent(regionPath);

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