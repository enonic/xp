module api.content.page.region {

    export class LayoutDescriptorDropdown extends DescriptorBasedDropdown<LayoutDescriptor> {

        constructor(name: string, loader: LayoutDescriptorLoader) {

            super(name, loader, {
                optionDisplayValueViewer: new LayoutDescriptorViewer(),
                dataIdProperty: "value",
                noOptionsText: "No layouts available"
            });
        }
    }
}
