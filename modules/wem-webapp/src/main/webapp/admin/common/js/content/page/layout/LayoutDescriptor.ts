module api_content_page_image {

    export class LayoutDescriptor extends api_content_page.ComponentDescriptor {

    }

    export class LayoutDescriptorBuilder extends api_content_page.ComponentDescriptorBuilder {

        public fromJson(json: api_content_page_layout_json.LayouteDescriptorJson): LayoutDescriptorBuilder {

            this.setName(new api_content_page.ComponentDescriptorName(json.name));
            this.setDisplayName(json.displayName);
            this.setControllerResource(api_module.ModuleResourceKey.fromString(json.controller));
            this.setForm(json.configForm != null ? new api_form.Form(json.configForm) : null);
            return this;
        }

        public setName(value: api_content_page.ComponentDescriptorName): LayoutDescriptorBuilder {
            this.name = value;
            return this;
        }

        public setDisplayName(value: string): LayoutDescriptorBuilder {
            this.displayName = value;
            return this;
        }

        public setForm(value: api_form.Form): LayoutDescriptorBuilder {
            this.form = value;
            return this;
        }

        public setControllerResource(value: api_module.ModuleResourceKey): LayoutDescriptorBuilder {
            this.controllerResource = value;
            return this;
        }

        public build(): LayoutDescriptor {
            return new LayoutDescriptor(this);
        }
    }
}