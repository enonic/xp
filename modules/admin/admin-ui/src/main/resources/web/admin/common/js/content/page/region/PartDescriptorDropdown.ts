import ApplicationKey = api.application.ApplicationKey;

module api.content.page.region {

    export class PartDescriptorDropdown extends DescriptorBasedDropdown<PartDescriptor> {

        protected loader: PartDescriptorLoader;
        
        constructor() {

            super({
                optionDisplayValueViewer: new PartDescriptorViewer(),
                dataIdProperty: "value",
                noOptionsText: "No parts available"
            });
        }

        loadDescriptors(applicationKeys: ApplicationKey[]) {
            this.loader.setApplicationKeys(applicationKeys);

            super.load();
        }
        
        protected createLoader(): PartDescriptorLoader {
            return new PartDescriptorLoader();
        }
    }
}
