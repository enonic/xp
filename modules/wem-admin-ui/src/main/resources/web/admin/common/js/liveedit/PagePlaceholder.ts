module api.liveedit {

    import GetPageDescriptorsByModulesRequest = api.content.page.GetPageDescriptorsByModulesRequest;
    import PageDescriptor = api.content.page.PageDescriptor;
    import PageController = api.content.page.inputtype.pagecontroller.PageController;
    import PageDescriptorDropdown = api.content.page.PageDescriptorDropdown;
    import PageDescriptorsJson = api.content.page.PageDescriptorsJson;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;

    export class PagePlaceholder extends api.dom.DivEl {

        private pageView: PageView;

        private controllerDropdown: api.content.page.PageDescriptorDropdown;

        constructor(pageView: PageView) {
            super();
            this.addClass("page-placeholder");
            this.pageView = pageView;

            var moduleKeys = pageView.getSite().getModuleKeys(),
                request = new GetPageDescriptorsByModulesRequest(moduleKeys),
                loader = new api.util.loader.BaseLoader<PageDescriptorsJson, PageDescriptor>(request);

            this.controllerDropdown = new PageDescriptorDropdown('page-controller', {
                loader: loader
            });

            this.controllerDropdown.hide();

            this.controllerDropdown.onOptionSelected((event: api.ui.selector.OptionSelectedEvent<api.content.page.PageDescriptor>) => {

                this.pageView.getPageModel().setController(event.getOption().displayValue, this);
            });

            this.appendChild(this.controllerDropdown);

            this.controllerDropdown.onClicked((event: MouseEvent) => {
                this.select();
                event.stopPropagation();
            });
        }

        select() {
            this.controllerDropdown.show();
            this.controllerDropdown.giveFocus();
        }

        deselect() {
            this.controllerDropdown.hide();
        }
    }
}