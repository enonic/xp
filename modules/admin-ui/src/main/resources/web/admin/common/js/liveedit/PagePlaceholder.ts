module api.liveedit {

    import GetPageDescriptorsByModulesRequest = api.content.page.GetPageDescriptorsByModulesRequest;
    import PageDescriptor = api.content.page.PageDescriptor;
    import SetController = api.content.page.SetController;
    import SiteModel = api.content.site.SiteModel;
    import PageController = api.content.page.inputtype.pagecontroller.PageController;
    import PageDescriptorDropdown = api.content.page.PageDescriptorDropdown;
    import PageDescriptorsJson = api.content.page.PageDescriptorsJson;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;

    export class PagePlaceholder extends ItemViewPlaceholder {

        private pageView: PageView;

        private controllerDropdown: api.content.page.PageDescriptorDropdown;

        constructor(pageView: PageView) {
            super();
            this.addClass("page-placeholder");
            this.pageView = pageView;

            var moduleKeys = pageView.liveEditModel.getSiteModel().getModuleKeys(),
                request = new GetPageDescriptorsByModulesRequest(moduleKeys),
                loader = new api.util.loader.BaseLoader<PageDescriptorsJson, PageDescriptor>(request);

            this.controllerDropdown = new PageDescriptorDropdown('page-controller', {
                loader: loader
            });
            this.controllerDropdown.load();

            this.controllerDropdown.onOptionSelected((event: api.ui.selector.OptionSelectedEvent<api.content.page.PageDescriptor>) => {

                var setController = new SetController(this).
                    setDescriptor(event.getOption().displayValue);
                this.pageView.liveEditModel.getPageModel().setController(setController);
            });

            this.appendChild(this.controllerDropdown);

            this.controllerDropdown.onClicked((event: MouseEvent) => {
                this.select();
                event.stopPropagation();
            });

            pageView.liveEditModel.getSiteModel().onPropertyChanged((event: api.PropertyChangedEvent) => {
                if (event.getPropertyName() == SiteModel.PROPERTY_NAME_SITE_CONFIGS) {
                    request.setModuleKeys(pageView.liveEditModel.getSiteModel().getModuleKeys());
                    loader.load();
                }
            });
        }

        select() {
            this.controllerDropdown.giveFocus();
        }

        deselect() {

        }
    }
}