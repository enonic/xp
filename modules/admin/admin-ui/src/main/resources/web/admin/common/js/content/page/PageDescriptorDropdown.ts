module api.content.page {

    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;
    import Option = api.ui.selector.Option;
    import SiteModel = api.content.site.SiteModel;
    import LiveEditModel = api.liveedit.LiveEditModel;
    import DescriptorBasedDropdown = api.content.page.region.DescriptorBasedDropdown;
    import ApplicationRemovedEvent = api.content.site.ApplicationRemovedEvent;

    export class PageDescriptorDropdown extends DescriptorBasedDropdown<PageDescriptor> {

        private loadedDataListeners: {(event: LoadedDataEvent<PageDescriptor>):void}[];

        private liveEditModel: LiveEditModel;

        constructor(model: LiveEditModel) {

            this.loadedDataListeners = [];

            this.liveEditModel = model;

            super('page-controller', this.createLoader(), {
                optionDisplayValueViewer: new PageDescriptorViewer(),
                dataIdProperty: 'value'
            });

            this.initListeners();
        }

        private createLoader(): api.util.loader.BaseLoader<PageDescriptorsJson, PageDescriptor> {
            var request = new GetPageDescriptorsByApplicationsRequest(this.liveEditModel.getSiteModel().getApplicationKeys());

            return new api.util.loader.BaseLoader<PageDescriptorsJson, PageDescriptor>(request).setComparator(
                new api.content.page.DescriptorByDisplayNameComparator());
        }

        handleLoadedData(event: LoadedDataEvent<PageDescriptor>) {
            super.handleLoadedData(event);
            this.notifyLoadedData(event);
        }

        private initListeners() {
            this.onOptionSelected((event: api.ui.selector.OptionSelectedEvent<api.content.page.PageDescriptor>) => {
                var pageDescriptor = event.getOption().displayValue;
                var setController = new SetController(this).setDescriptor(pageDescriptor);
                this.liveEditModel.getPageModel().setController(setController);
            });

            var onApplicationAddedHandler = () => {
                this.updateRequestApplicationKeys();
                this.load();
            }

            var onApplicationRemovedHandler = (event: ApplicationRemovedEvent) => {

                this.updateRequestApplicationKeys();
                this.load();

                let currentController = this.liveEditModel.getPageModel().getController();
                if (currentController) {
                    let removedApp = event.getApplicationKey();
                    if (removedApp.equals(currentController.getKey().getApplicationKey())) {
                        this.liveEditModel.getPageModel().reset();
                    }
                }
            }

            this.liveEditModel.getSiteModel().onApplicationAdded(onApplicationAddedHandler);

            this.liveEditModel.getSiteModel().onApplicationRemoved(onApplicationRemovedHandler);

            this.onRemoved(() => {
                this.liveEditModel.getSiteModel().unApplicationAdded(onApplicationAddedHandler);
                this.liveEditModel.getSiteModel().unApplicationRemoved(onApplicationRemovedHandler);
            })
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

        private updateRequestApplicationKeys() {
            (<GetPageDescriptorsByApplicationsRequest>this.getLoader().getRequest()).setApplicationKeys(
                this.liveEditModel.getSiteModel().getApplicationKeys());
        }

    }
}