module api.content.page.image {

    import Dropdown = api.ui.selector.dropdown.Dropdown;
    import DropdownConfig = api.ui.selector.dropdown.DropdownConfig;
    import Option = api.ui.selector.Option;
    import DescriptorKey = api.content.page.DescriptorKey;
    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;

    export interface ImageDescriptorDropdownConfig {

        loader: ImageDescriptorLoader
    }

    export class ImageDescriptorDropdown extends Dropdown<ImageDescriptor> {

        constructor(name: string, config: ImageDescriptorDropdownConfig) {

            super(name, <DropdownConfig<ImageDescriptor>>{
                optionDisplayValueViewer: new ImageDescriptorViewer(),
                dataIdProperty: "value"
            });

            config.loader.onLoadedData((event: LoadedDataEvent<ImageDescriptor>) => {

                var descriptors: ImageDescriptor[] = event.getData();
                descriptors.forEach((descriptor: ImageDescriptor) => {
                    var option = <Option<ImageDescriptor>>{
                        value: descriptor.getKey().toString(),
                        displayValue: descriptor};

                    this.addOption(option);
                });
            });
        }

        setDescriptor(key: DescriptorKey) {
            var option = this.getOptionByValue(key.toString());
            if (option) {
                this.selectOption(option);
            }
        }
    }
}