module api.content.page.region {

    export class LayoutDescriptorDropdown extends DescriptorBasedDropdown<LayoutDescriptor> {

        protected loader: LayoutDescriptorLoader;

        constructor() {

            super({
                optionDisplayValueViewer: new LayoutDescriptorViewer(),
                dataIdProperty: "value",
                noOptionsText: "No layouts available"
            });
        }

        loadDescriptors(applicationKeys: ApplicationKey[]) {
            this.loader.setApplicationKeys(applicationKeys);

            super.load();
        }

        protected createLoader(): LayoutDescriptorLoader {
            return new LayoutDescriptorLoader();
        }
    }
}
