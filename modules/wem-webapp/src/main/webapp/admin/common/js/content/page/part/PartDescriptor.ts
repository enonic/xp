module api.content.page.part {

    export class PartDescriptor extends api.content.page.Descriptor {

    }

    export class PartDescriptorBuilder extends api.content.page.DescriptorBuilder {

        public fromJson(json: api.content.page.part.json.PartDescriptorJson): PartDescriptorBuilder {

            this.setName(new api.content.page.DescriptorName(json.name));
            this.setDisplayName(json.displayName);
            this.setConfig(json.config != null ? new api.form.Form(json.config) : null);
            return this;
        }

        public setName(value: api.content.page.DescriptorName): PartDescriptorBuilder {
            this.name = value;
            return this;
        }

        public setDisplayName(value: string): PartDescriptorBuilder {
            this.displayName = value;
            return this;
        }

        public setConfig(value: api.form.Form): PartDescriptorBuilder {
            this.config = value;
            return this;
        }

        public build(): PartDescriptor {
            return new PartDescriptor(this);
        }
    }
}