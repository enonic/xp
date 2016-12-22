module api.content.page {

    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;
    import Option = api.ui.selector.Option;
    import SiteModel = api.content.site.SiteModel;
    import LiveEditModel = api.liveedit.LiveEditModel;
    import DescriptorBasedDropdown = api.content.page.region.DescriptorBasedDropdown;
    import ApplicationRemovedEvent = api.content.site.ApplicationRemovedEvent;

    export class PageDescriptorDropdown extends DescriptorBasedDropdown<PageDescriptor> {

        private loadedDataListeners: {(event: LoadedDataEvent<PageDescriptor>): void}[];

        private liveEditModel: LiveEditModel;

        protected loader: PageDescriptorLoader;

        constructor(model: LiveEditModel) {
            super({
                optionDisplayValueViewer: new PageDescriptorViewer(),
                dataIdProperty: 'value'
            }, 'page-controller');

            this.loadedDataListeners = [];
            this.liveEditModel = model;

            this.initListeners();
        }

        load() {
            this.loader.setApplicationKeys(this.liveEditModel.getSiteModel().getApplicationKeys());

            super.load();
        }

        protected createLoader(): PageDescriptorLoader {
            return new PageDescriptorLoader();
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
                this.load();
            };

            var onApplicationRemovedHandler = (event: ApplicationRemovedEvent) => {

                let currentController = this.liveEditModel.getPageModel().getController();
                let removedApp = event.getApplicationKey();
                if (currentController && removedApp.equals(currentController.getKey().getApplicationKey())) {
                    // no need to load as current controller's app was removed
                    this.liveEditModel.getPageModel().reset();
                } else {
                    this.load();
                }
            };

            this.liveEditModel.getSiteModel().onApplicationAdded(onApplicationAddedHandler);

            this.liveEditModel.getSiteModel().onApplicationRemoved(onApplicationRemovedHandler);

            this.onRemoved(() => {
                this.liveEditModel.getSiteModel().unApplicationAdded(onApplicationAddedHandler);
                this.liveEditModel.getSiteModel().unApplicationRemoved(onApplicationRemovedHandler);
            });
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