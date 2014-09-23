module api.content.page {

    import Dropdown = api.ui.selector.dropdown.Dropdown;
    import DropdownConfig = api.ui.selector.dropdown.DropdownConfig;
    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;

    export interface PageDescriptorDropdownConfig {

        loader: api.util.loader.BaseLoader<PageDescriptorsJson, PageDescriptor>

    }

    export class PageDescriptorDropdown extends Dropdown<PageDescriptor> {

        constructor(name:string, config: PageDescriptorDropdownConfig) {
            super(name, <DropdownConfig<PageDescriptor>>{
                optionDisplayValueViewer: new PageDescriptorViewer(),
                dataIdProperty: 'value'
            });

            config.loader.onLoadedData((event: LoadedDataEvent<PageDescriptor>) => {
                event.getData().forEach((descriptor: PageDescriptor) => {
                    this.addOption({
                        value: descriptor.getKey().toString(),
                        displayValue: descriptor,
                        indices: [descriptor.getDisplayName(), descriptor.getName().toString()]
                    });
                });
            });

            config.loader.load();
        }

    }
}