module api.liveedit {

    import GetPageDescriptorsByApplicationsRequest = api.content.page.GetPageDescriptorsByApplicationsRequest;
    import PageDescriptor = api.content.page.PageDescriptor;
    import SetController = api.content.page.SetController;
    import SiteModel = api.content.site.SiteModel;
    import PageDescriptorDropdown = api.content.page.PageDescriptorDropdown;
    import PageDescriptorsJson = api.content.page.PageDescriptorsJson;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;

    export class PagePlaceholder extends ItemViewPlaceholder {

        private pageView: PageView;

        private infoBlock: api.dom.DivEl;

        private controllerDropdown: api.content.page.PageDescriptorDropdown;

        constructor(pageView: PageView) {
            super();
            this.addClass("page-placeholder");
            this.pageView = pageView;

            this.controllerDropdown = new PageDescriptorDropdown(pageView.liveEditModel);
            this.controllerDropdown.load();

            this.appendChild(this.controllerDropdown);

            if (!this.pageView.liveEditModel.getPageModel().isPageTemplate()) {
                this.infoBlock = this.initInfoBlock();
                this.appendChild(this.infoBlock);
            }

            this.controllerDropdown.onClicked((event: MouseEvent) => {
                this.select();
                event.stopPropagation();
            });

        }

        select() {
            this.controllerDropdown.giveFocus();
        }

        deselect() {

        }

        private initInfoBlock(): api.dom.DivEl {
            var wrapperDiv = new api.dom.DivEl("page-placeholder-info");

            var line1 = new api.dom.DivEl("page-placeholder-info-line1");
            line1.setHtml('No template found for "' + this.pageView.getLocalType() + '"');
            var line2 = new api.dom.DivEl("page-placeholder-info-line2");
            line2.setHtml("Select a controller below to setup a page for this item");

            wrapperDiv.appendChildren(line1, line2);

            return wrapperDiv;
        }
    }
}