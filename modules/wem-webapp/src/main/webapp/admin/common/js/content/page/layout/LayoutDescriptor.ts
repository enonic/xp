module api.content.page.layout {

    export class LayoutDescriptor extends api.content.page.Descriptor {

    }

    export class LayoutDescriptorBuilder extends api.content.page.DescriptorBuilder {

        public fromJson(json: api.content.page.layout.json.LayoutDescriptorJson): LayoutDescriptorBuilder {

            this.setName(new api.content.page.DescriptorName(json.name));
            this.setDisplayName(json.displayName);
            this.setConfig(json.config != null ? new api.form.Form(json.config) : null);
            return this;
        }

        public setName(value: api.content.page.DescriptorName): LayoutDescriptorBuilder {
            this.name = value;
            return this;
        }

        public setDisplayName(value: string): LayoutDescriptorBuilder {
            this.displayName = value;
            return this;
        }

        public setConfig(value: api.form.Form): LayoutDescriptorBuilder {
            this.config = value;
            return this;
        }

        public build(): LayoutDescriptor {
            return new LayoutDescriptor(this);
        }
    }
}