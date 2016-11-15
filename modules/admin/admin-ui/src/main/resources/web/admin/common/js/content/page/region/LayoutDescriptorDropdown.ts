module api.content.page.region {

    export class LayoutDescriptorDropdown extends DescriptorBasedDropdown<LayoutDescriptor> {

        constructor(name: string) {

            super(name, {
                optionDisplayValueViewer: new LayoutDescriptorViewer(),
                dataIdProperty: "value",
                noOptionsText: "No layouts available"
            });
        }
    }
}
