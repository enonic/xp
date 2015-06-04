module api.content.page.region {

    import Dropdown = api.ui.selector.dropdown.Dropdown;
    import DropdownConfig = api.ui.selector.dropdown.DropdownConfig;
    import Option = api.ui.selector.Option;
    import DescriptorKey = api.content.page.DescriptorKey;
    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;

    export interface PartDescriptorDropdownConfig {

        loader: PartDescriptorLoader
    }

    export class PartDescriptorDropdown extends Dropdown<PartDescriptor> {

        constructor(name: string, config: PartDescriptorDropdownConfig) {

            super(name, <DropdownConfig<PartDescriptor>>{
                optionDisplayValueViewer: new PartDescriptorViewer(),
                dataIdProperty: "value"
            });

            config.loader.onLoadedData((event: LoadedDataEvent<PartDescriptor>) => {

                var descriptors: PartDescriptor[] = event.getData();
                descriptors.forEach((descriptor: PartDescriptor) => {

                    var indices: string[] = [];
                    indices.push(descriptor.getDisplayName());
                    indices.push(descriptor.getName().toString());

                    var option = <Option<PartDescriptor>>{
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

        getDescriptor(descriptorKey: DescriptorKey): PartDescriptor {
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
