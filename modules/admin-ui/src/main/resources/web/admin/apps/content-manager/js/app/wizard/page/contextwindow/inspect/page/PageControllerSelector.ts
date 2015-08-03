module app.wizard.page.contextwindow.inspect.page {

    import PropertyChangedEvent = api.PropertyChangedEvent;
    import LiveEditModel = api.liveedit.LiveEditModel;
    import SiteModel = api.content.site.SiteModel;
    import PageModel = api.content.page.PageModel;
    import SetController = api.content.page.SetController;
    import PageDescriptor = api.content.page.PageDescriptor;
    import DescriptorKey = api.content.page.DescriptorKey;
    import PageDescriptorsJson = api.content.page.PageDescriptorsJson;
    import PageDescriptorDropdown = api.content.page.PageDescriptorDropdown;
    import GetPageDescriptorsByApplicationsRequest = api.content.page.GetPageDescriptorsByApplicationsRequest;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;
    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;

    export class PageControllerSelector extends PageDescriptorDropdown {

        private getPageDescriptorsByModulesRequest: GetPageDescriptorsByApplicationsRequest;

        private pageModel: PageModel;

        private siteModel: SiteModel;

        constructor() {

            this.getPageDescriptorsByModulesRequest = new GetPageDescriptorsByApplicationsRequest([]);

            super('page-controller', {
                loader: new api.util.loader.BaseLoader<PageDescriptorsJson, PageDescriptor>(this.getPageDescriptorsByModulesRequest).
                    setComparator(new api.content.page.DescriptorByDisplayNameComparator())
            });

            this.onOptionSelected((event: OptionSelectedEvent<PageDescriptor>) => {
                var pageDescriptor = event.getOption().displayValue;
                var setController = new SetController(this).setDescriptor(pageDescriptor);
                this.pageModel.setController(setController);
            });
        }

        setModel(model: LiveEditModel) {

            this.siteModel = model.getSiteModel();
            this.pageModel = model.getPageModel();

            this.getPageDescriptorsByModulesRequest.setApplicationKeys(this.siteModel.getApplicationKeys());
            this.onLoadedData((event: LoadedDataEvent<PageDescriptor>) => {

                if (this.pageModel.hasController()) {
                    this.selectController(this.pageModel.getController().getKey());
                }
            });
            this.load();

            this.siteModel.onPropertyChanged((event: api.PropertyChangedEvent) => {
                if (event.getPropertyName() == SiteModel.PROPERTY_NAME_SITE_CONFIGS) {
                    this.getPageDescriptorsByModulesRequest.setApplicationKeys(this.siteModel.getApplicationKeys());
                    this.load();
                }
            });

            this.pageModel.onPropertyChanged((event: PropertyChangedEvent) => {
                if (event.getPropertyName() == PageModel.PROPERTY_CONTROLLER && this !== event.getSource()) {
                    var descriptorKey = <DescriptorKey>event.getNewValue();
                    if (descriptorKey) {
                        this.selectController(descriptorKey);
                    }
                    // TODO: Change class to extend a PageDescriptorComboBox instead, since we then can deselect.
                }
            });
        }

        private selectController(descriptorKey: DescriptorKey) {

            var optionToSelect = this.getOptionByValue(descriptorKey.toString());
            if (optionToSelect) {
                this.selectOption(optionToSelect, true);
            }
        }
    }
}