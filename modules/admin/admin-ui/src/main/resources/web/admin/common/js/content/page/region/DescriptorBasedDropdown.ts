module api.content.page.region {

    import Dropdown = api.ui.selector.dropdown.Dropdown;
    import DropdownConfig = api.ui.selector.dropdown.DropdownConfig;
    import Option = api.ui.selector.Option;
    import DescriptorKey = api.content.page.DescriptorKey;
    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;

    export class DescriptorBasedDropdown<DESCRIPTOR extends Descriptor> extends Dropdown<DESCRIPTOR> {

        private loader: api.util.loader.BaseLoader<any, DESCRIPTOR>;

        constructor(name: string, loader: api.util.loader.BaseLoader<any, DESCRIPTOR>, dropdownConfig: DropdownConfig<DESCRIPTOR>) {

            super(name, dropdownConfig);

            this.loader = loader;

            this.initLoaderListeners();
        }

        private initLoaderListeners() {
            this.loader.onLoadedData((event: LoadedDataEvent<DESCRIPTOR>) => {
                this.setOptions(this.createOptions(event.getData()));
            });

            this.loader.onLoadingData((event: api.util.loader.event.LoadingDataEvent) => {
                this.setEmptyDropdownText("Searching...");
            });
        }

        private createOptions(descriptors: DESCRIPTOR[]): Option<DESCRIPTOR>[] {
            var options = [];

            descriptors.forEach((descriptor: DESCRIPTOR) => {
                options.push(this.createOption(descriptor));
            });

            return options;
        }

        private createOption(descriptor: DESCRIPTOR): Option<DESCRIPTOR> {
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

        showDropdown() {
            super.showDropdown();
            this.loadOptionsAfterShowDropdown();
        }

        private loadOptionsAfterShowDropdown() {
            this.loader.load();
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
