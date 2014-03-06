module api.content.page.text {

    export class TextDescriptor extends api.content.page.Descriptor {

    }

    export class TextDescriptorBuilder extends api.content.page.DescriptorBuilder {

        public fromJson(json: api.content.page.text.json.TextDescriptorJson): TextDescriptorBuilder {

            this.setKey(api.content.page.DescriptorKey.fromString(json.key))
            this.setName(new api.content.page.DescriptorName(json.name));
            this.setDisplayName(json.displayName);
            this.setConfig(json.config != null ? new api.form.Form(json.config) : null);
            return this;
        }

        public setKey(value: api.content.page.DescriptorKey): TextDescriptorBuilder {
            this.key = value;
            return this;
        }

        public setName(value: api.content.page.DescriptorName): TextDescriptorBuilder {
            this.name = value;
            return this;
        }

        public setDisplayName(value: string): TextDescriptorBuilder {
            this.displayName = value;
            return this;
        }

        public setConfig(value: api.form.Form): TextDescriptorBuilder {
            this.config = value;
            return this;
        }

        public build(): TextDescriptor {
            return new TextDescriptor(this);
        }
    }
}