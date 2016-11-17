module api.content.page.region {

    import DropdownConfig = api.ui.selector.dropdown.DropdownConfig;
    import Option = api.ui.selector.Option;
    import DescriptorKey = api.content.page.DescriptorKey;
    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;
    import RichDropdown = api.ui.selector.dropdown.RichDropdown;

    export class DescriptorBasedDropdown<DESCRIPTOR extends Descriptor> extends RichDropdown<DESCRIPTOR> {

        protected createOption(descriptor: DESCRIPTOR): Option<DESCRIPTOR> {
            var indices: string[] = [];
            indices.push(descriptor.getDisplayName());
            indices.push(descriptor.getName().toString());

            var option = <Option<DESCRIPTOR>>{
                value: descriptor.getKey().toString(),
                displayValue: descriptor,
                indices: indices
            };

            return option;
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

        getDescriptor(descriptorKey: DescriptorKey): DESCRIPTOR {
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
