module api.liveedit {

    import PageDescriptor = api.content.page.PageDescriptor;
    import PageDescriptorDropdown = api.content.page.PageDescriptorDropdown;
    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;
    import GetContentTypeByNameRequest = api.schema.content.GetContentTypeByNameRequest;
    import ContentType = api.schema.content.ContentType;

    export class PagePlaceholder extends ItemViewPlaceholder {

        private pageView: PageView;

        private infoBlock: PagePlaceholderInfoBlock;

        private controllerDropdown: api.content.page.PageDescriptorDropdown;

        constructor(pageView: PageView) {
            super();
            this.addClassEx("page-placeholder");
            this.pageView = pageView;

            this.controllerDropdown = new PageDescriptorDropdown(pageView.liveEditModel);
            this.controllerDropdown.load();
            this.appendChild(this.controllerDropdown);

            this.infoBlock = new PagePlaceholderInfoBlock();
            this.appendChild(this.infoBlock);


            this.controllerDropdown.onLoadedData((event: LoadedDataEvent<PageDescriptor>) => {
                if (event.getData().length > 0) {
                    this.controllerDropdown.show();
                    var content = this.pageView.liveEditModel.getContent();
                    if (!content.isPageTemplate()) {
                        new GetContentTypeByNameRequest(content.getType()).sendAndParse().then((contentType: ContentType) => {
                            this.infoBlock.setTextForContent(contentType.getDisplayName());
                        }).catch((reason)=> {
                            this.infoBlock.setTextForContent(content.getType().toString());
                            api.DefaultErrorHandler.handle(reason);
                        }).done();
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