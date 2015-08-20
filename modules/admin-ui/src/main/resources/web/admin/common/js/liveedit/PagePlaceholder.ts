module api.liveedit {

    import PageDescriptor = api.content.page.PageDescriptor;
    import PageDescriptorDropdown = api.content.page.PageDescriptorDropdown;
    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;

    export class PagePlaceholder extends ItemViewPlaceholder {

        private pageView: PageView;

        private infoBlock: PagePlaceholderInfoBlock;

        private controllerDropdown: api.content.page.PageDescriptorDropdown;

        constructor(pageView: PageView) {
            super();
            this.addClass("page-placeholder");
            this.pageView = pageView;

            this.controllerDropdown = new PageDescriptorDropdown(pageView.liveEditModel);
            this.controllerDropdown.load();
            this.appendChild(this.controllerDropdown);

            this.infoBlock = new PagePlaceholderInfoBlock();
            this.appendChild(this.infoBlock);


            this.controllerDropdown.onLoadedData((event: LoadedDataEvent<PageDescriptor>) => {
                if(event.getData().length > 0) {
                    this.controllerDropdown.show();
                    if(!this.pageView.liveEditModel.getContent().isPageTemplate()) {
                        this.infoBlock.setTextForContent(this.pageView.getLocalType(), this.pageView.getName());
                    }
                }
                else {
                    this.controllerDropdown.hide();
                    this.infoBlock.setNoControllersAvailableText();
                }
            });



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
    }
}