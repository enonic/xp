module api.content.page {

    import Dropdown = api.ui.selector.dropdown.Dropdown;
    import DropdownConfig = api.ui.selector.dropdown.DropdownConfig;
    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;
    import Option = api.ui.selector.Option;
    import SiteModel = api.content.site.SiteModel;
    import LiveEditModel = api.liveedit.LiveEditModel;

    export class PageDescriptorDropdown extends Dropdown<PageDescriptor> {

        private loader: api.util.loader.BaseLoader<PageDescriptorsJson, PageDescriptor>;

        private loadedDataListeners: {(event: LoadedDataEvent<PageDescriptor>):void}[] = [];

        private getPageDescriptorsByApplicationsRequest: GetPageDescriptorsByApplicationsRequest;

        private siteModel: SiteModel;

        constructor(model: LiveEditModel) {
            super('page-controller', <DropdownConfig<PageDescriptor>>{
                optionDisplayValueViewer: new PageDescriptorViewer(),
                dataIdProperty: 'value'
            });

            this.siteModel = model.getSiteModel();

            this.initLoader();

            this.onOptionSelected((event: api.ui.selector.OptionSelectedEvent<api.content.page.PageDescriptor>) => {
                var pageDescriptor = event.getOption().displayValue;
                var setController = new SetController(this).setDescriptor(pageDescriptor);
                model.getPageModel().setController(setController);
            });

            this.siteModel.onApplicationAdded((event: api.content.site.ApplicationAddedEvent) => {
                this.getPageDescriptorsByApplicationsRequest.setApplicationKeys(this.siteModel.getApplicationKeys());
                this.removeAllOptions();
                this.load();
            });

            this.siteModel.onApplicationRemoved((event: api.content.site.ApplicationRemovedEvent) => {
                this.getPageDescriptorsByApplicationsRequest.setApplicationKeys(this.siteModel.getApplicationKeys());
                this.removeAllOptions();
                this.load();
            });
        }

        private initLoader() {
            this.getPageDescriptorsByApplicationsRequest = new GetPageDescriptorsByApplicationsRequest(this.siteModel.getApplicationKeys());

            this.loader = new api.util.loader.BaseLoader<PageDescriptorsJson, PageDescriptor>(this.getPageDescriptorsByApplicationsRequest).
                setComparator(new api.content.page.DescriptorByDisplayNameComparator());

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