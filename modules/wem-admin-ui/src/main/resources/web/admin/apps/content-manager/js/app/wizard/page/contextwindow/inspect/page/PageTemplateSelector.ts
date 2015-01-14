module app.wizard.page.contextwindow.inspect.page {

    import PropertyChangedEvent = api.PropertyChangedEvent;
    import ContentId = api.content.ContentId;
    import PageTemplateKey = api.content.page.PageTemplateKey;
    import PageTemplate = api.content.page.PageTemplate;
    import Option = api.ui.selector.Option;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;
    import Dropdown = api.ui.selector.dropdown.Dropdown;
    import DropdownConfig = api.ui.selector.dropdown.DropdownConfig;
    import PageModel = api.content.page.PageModel;
    import LiveEditModel = api.liveedit.LiveEditModel;

    export class PageTemplateSelector extends Dropdown<PageTemplateOption> {

        private pageModel: PageModel;

        private selectionListeners: {(template: PageTemplate):void}[] = [];

        constructor() {
            super("pageTemplate", <DropdownConfig<PageTemplateOption>>{
                optionDisplayValueViewer: new PageTemplateOptionViewer()
            });
        }

        setModel(liveEditModel: LiveEditModel) {

            this.pageModel = liveEditModel.getPageModel();

            var pageTemplateOptions = new PageTemplateOptions(liveEditModel.getSiteModel().getSiteId(),
                liveEditModel.getContent().getType(), this.pageModel);
            
            pageTemplateOptions.getOptions().
                then((options: Option<PageTemplateOption>[]) => {

                    options.forEach((option: Option<PageTemplateOption>) => {
                        this.addOption(option);
                    });

                    if (this.pageModel.hasTemplate()) {
                        this.selectTemplate(this.pageModel.getTemplateKey());
                    }
                    else {
                        this.selectOption(pageTemplateOptions.getDefault(), true);
                    }

                    this.onOptionSelected((event: OptionSelectedEvent<PageTemplateOption>) => {
                        var pageTemplate = event.getOption().displayValue.getPageTemplate();
                        this.notifySelection(pageTemplate);
                    });

                    this.pageModel.onPropertyChanged((event: PropertyChangedEvent) => {
                        if (event.getPropertyName() == "template" && this !== event.getSource()) {
                            var pageTemplateKey = <PageTemplateKey>event.getNewValue();
                            if (pageTemplateKey) {
                                this.selectTemplate(pageTemplateKey);
                            } else {
                                this.selectOption(pageTemplateOptions.getDefault());
                            }
                        }
                    });

                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                }).done();
        }

        private selectTemplate(template: PageTemplateKey) {
            var optionToSelect = this.getOptionByValue(template.toString());
            if (optionToSelect) {
                this.selectOption(optionToSelect, true);
            }
        }

        onSelection(listener: (event: PageTemplate)=>void) {
            this.selectionListeners.push(listener);
        }

        unSelection(listener: (event: PageTemplate)=>void) {
            this.selectionListeners.filter((currentListener: (event: PageTemplate)=>void) => {
                return listener != currentListener;
            });
        }

        private notifySelection(item: PageTemplate) {
            this.selectionListeners.forEach((listener: (event: PageTemplate)=>void) => {
                listener(item);
            });
        }
    }
}
