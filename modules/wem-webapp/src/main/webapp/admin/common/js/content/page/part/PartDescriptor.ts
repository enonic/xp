module api.content.page.part {

    export class PartDescriptor extends api.content.page.ComponentDescriptor {

    }

    export class PartDescriptorBuilder extends api.content.page.ComponentDescriptorBuilder {

        public fromJson(json: api.content.page.part.json.PartDescriptorJson): PartDescriptorBuilder {

            this.setName(new api.content.page.ComponentDescriptorName(json.name));
            this.setDisplayName(json.displayName);
            this.setForm(json.configForm != null ? new api.form.Form(json.configForm) : null);
            return this;
        }

        public setName(value:api.content.page.ComponentDescriptorName):PartDescriptorBuilder {
            this.name = value;
            return this;
        }

        public setDisplayName(value:string):PartDescriptorBuilder {
            this.displayName = value;
            return this;
        }

        public setForm(value:api.form.Form):PartDescriptorBuilder {
            this.form = value;
            return this;
        }

        public build(): PartDescriptor {
            return new PartDescriptor(this);
        }
    }
}