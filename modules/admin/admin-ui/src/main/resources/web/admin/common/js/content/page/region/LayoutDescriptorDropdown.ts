module api.content.page.region {

    import Dropdown = api.ui.selector.dropdown.Dropdown;
    import DropdownConfig = api.ui.selector.dropdown.DropdownConfig;
    import Option = api.ui.selector.Option;
    import DescriptorKey = api.content.page.DescriptorKey;
    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;

    export interface LayoutDescriptorDropdownConfig {

        loader: LayoutDescriptorLoader
    }

    export class LayoutDescriptorDropdown extends Dropdown<LayoutDescriptor> {

        constructor(name: string, config: LayoutDescriptorDropdownConfig) {

            super(name, <DropdownConfig<LayoutDescriptor>>{
                optionDisplayValueViewer: new LayoutDescriptorViewer(),
                dataIdProperty: "value"
            });

            config.loader.onLoadedData((event: LoadedDataEvent<LayoutDescriptor>) => {

                var descriptors: LayoutDescriptor[] = event.getData();
                descriptors.forEach((descriptor: LayoutDescriptor) => {

                    var indices: string[] = [];
                    indices.push(descriptor.getDisplayName());
                    indices.push(descriptor.getName().toString());

                    var option = <Option<LayoutDescriptor>>{
                        value: descriptor.getKey().toString(),
                        displayValue: descriptor,
                        indices: indices
                    };

                    this.addOption(option);
                });
            });
        }

        setDescriptor(descriptor: Descriptor) {
            if (descriptor) {
                var option = this.getOptionByValue(descriptor.getKey().toString());
                if (option) {
                    this.selectOption(option, true);
                }
            } else {
                this.reset();
            }
        }

        getDescriptor(descriptorKey: DescriptorKey): LayoutDescriptor {
            if (descriptorKey) {
                var option = this.getOptionByValue(descriptorKey.toString());
                if (option) {
                    return option.displayValue;
                }
            }
            return null;
        }
    }
}
