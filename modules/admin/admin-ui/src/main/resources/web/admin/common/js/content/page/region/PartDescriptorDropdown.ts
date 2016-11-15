module api.content.page.region {

    export class PartDescriptorDropdown extends DescriptorBasedDropdown<PartDescriptor> {

        constructor(name: string) {

            super(name, {
                optionDisplayValueViewer: new PartDescriptorViewer(),
                dataIdProperty: "value",
                noOptionsText: "No parts available"
            });

        }
    }
}
