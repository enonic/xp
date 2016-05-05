module api.content.page.region {

    export class PartDescriptorDropdown extends DescriptorBasedDropdown<PartDescriptor> {

        constructor(name: string, loader: PartDescriptorLoader) {

            super(name, loader, {
                optionDisplayValueViewer: new PartDescriptorViewer(),
                dataIdProperty: "value",
                noOptionsText: "No parts available in required applications"
            });

        }
    }
}
