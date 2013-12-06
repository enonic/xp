module api_content_page {

    export class PageDescriptor extends ComponentDescriptor {

    }

    export class PageDescriptorBuilder extends ComponentDescriptorBuilder {

        public fromJson(json: api_content_page_json.PageDescriptorJson): PageDescriptorBuilder {

            this.setName(new ComponentDescriptorName(json.name));
            this.setDisplayName(json.displayName);
            this.setControllerResource(api_module.ModuleResourceKey.fromString(json.controller));
            this.setForm(json.configForm != null ? new api_form.Form(json.configForm) : null);
            return this;
        }

        public setName(value: ComponentDescriptorName): PageDescriptorBuilder {
            this.name = value;
            return this;
        }

        public setDisplayName(value: string): PageDescriptorBuilder {
            this.displayName = value;
            return this;
        }

        public setForm(value: api_form.Form): PageDescriptorBuilder {
            this.form = value;
            return this;
        }

        public setControllerResource(value: api_module.ModuleResourceKey): PageDescriptorBuilder {
            this.controllerResource = value;
            return this;
        }

        public build(): PageDescriptor {
            return new PageDescriptor(this);
        }
    }
}