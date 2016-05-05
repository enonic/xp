module api.content.page {

    import Dropdown = api.ui.selector.dropdown.Dropdown;
    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;
    import Option = api.ui.selector.Option;
    import SiteModel = api.content.site.SiteModel;
    import LiveEditModel = api.liveedit.LiveEditModel;

    export class PageDescriptorDropdown extends Dropdown<PageDescriptor> {

        private loader: api.util.loader.BaseLoader<PageDescriptorsJson, PageDescriptor>;

        private loadedDataListeners: {(event: LoadedDataEvent<PageDescriptor>):void}[] = [];

        private getPageDescriptorsByApplicationsRequest: GetPageDescriptorsByApplicationsRequest;

        private liveEditModel: LiveEditModel;

        constructor(model: LiveEditModel) {
            super('page-controller', {
                optionDisplayValueViewer: new PageDescriptorViewer(),
                dataIdProperty: 'value'
            });

            this.liveEditModel = model;

            this.initLoader();

            this.initListeners();
        }

        private initLoader() {
            this.getPageDescriptorsByApplicationsRequest =
                new GetPageDescriptorsByApplicationsRequest(this.liveEditModel.getSiteModel().getApplicationKeys());

            this.loader =
                new api.util.loader.BaseLoader<PageDescriptorsJson, PageDescriptor>(this.getPageDescriptorsByApplicationsRequest).setComparator(
                    new api.content.page.DescriptorByDisplayNameComparator());

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

        private createOption(descriptor: PageDescriptor): Option<PageDescriptor> {
            var indices: string[] = [];
            indices.push(descriptor.getDisplayName());
            indices.push(descriptor.getName().toString());

            var option = <Option<PageDescriptor>>{
                value: descriptor.getKey().toString(),
                displayValue: descriptor,
                indices: indices
            };

            return option;
        }

        private initListeners() {
            this.onOptionSelected((event: api.ui.selector.OptionSelectedEvent<api.content.page.PageDescriptor>) => {
                var pageDescriptor = event.getOption().displayValue;
                var setController = new SetController(this).setDescriptor(pageDescriptor);
                this.liveEditModel.getPageModel().setController(setController);
            });

            var onApplicationAddedHandler = () => {
                this.getPageDescriptorsByApplicationsRequest.setApplicationKeys(this.liveEditModel.getSiteModel().getApplicationKeys());
                this.removeAllOptions();
                this.load();
            }

            var onApplicationRemovedHandler = () => {
                this.getPageDescriptorsByApplicationsRequest.setApplicationKeys(this.liveEditModel.getSiteModel().getApplicationKeys());
                this.removeAllOptions();
                this.load();
            }

            this.liveEditModel.getSiteModel().onApplicationAdded(onApplicationAddedHandler);

            this.liveEditModel.getSiteModel().onApplicationRemoved(onApplicationRemovedHandler);

            this.onRemoved(() => {
                this.liveEditModel.getSiteModel().unApplicationAdded(onApplicationAddedHandler);
                this.liveEditModel.getSiteModel().unApplicationRemoved(onApplicationRemovedHandler);
            })
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