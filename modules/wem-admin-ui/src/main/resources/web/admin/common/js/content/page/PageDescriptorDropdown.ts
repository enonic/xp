module api.content.page {

    import Dropdown = api.ui.selector.dropdown.Dropdown;
    import DropdownConfig = api.ui.selector.dropdown.DropdownConfig;
    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;
    import Option = api.ui.selector.Option;

    export interface PageDescriptorDropdownConfig {

        loader: api.util.loader.BaseLoader<PageDescriptorsJson, PageDescriptor>
    }

    export class PageDescriptorDropdown extends Dropdown<PageDescriptor> {

        private loader: api.util.loader.BaseLoader<PageDescriptorsJson, PageDescriptor>;

        private loadedDataListeners: {(event: LoadedDataEvent<PageDescriptor>):void}[] = [];

        constructor(name: string, config: PageDescriptorDropdownConfig) {
            super(name, <DropdownConfig<PageDescriptor>>{
                optionDisplayValueViewer: new PageDescriptorViewer(),
                dataIdProperty: 'value'
            });

            this.loader = config.loader;
            this.loader.onLoadedData((event: LoadedDataEvent<PageDescriptor>) => {
                event.getData().forEach((descriptor: PageDescriptor) => {
                    var option = <Option<PageDescriptor>>{
                        value: descriptor.getKey().toString(),
                        displayValue: descriptor,
                        indices: [descriptor.getDisplayName(), descriptor.getName().toString()]
                    };
                    this.addOption(option);
                });
                this.notifyLoadedData(event);
            });
        }

        load() {
            this.loader.load();
        }

        onLoadedData(listener: (event: LoadedDataEvent<PageDescriptor>) => void) {
            this.loadedDataListeners.push(listener);
        }

        unLoadedData(listener: (event: LoadedDataEvent<PageDescriptor>) => void) {
            this.loadedDataListeners = this.loadedDataListeners.filter((currentListener: (event: LoadedDataEvent<PageDescriptor>)=>void)=> {
                return currentListener != listener;
            });
        }

        private notifyLoadedData(event: LoadedDataEvent<PageDescriptor>) {
            this.loadedDataListeners.forEach((listener: (event: LoadedDataEvent<PageDescriptor>)=>void)=> {
                listener.call(this, event);
            });
        }
    }
}