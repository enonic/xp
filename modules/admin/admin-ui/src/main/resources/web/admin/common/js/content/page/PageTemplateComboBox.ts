module api.content.page {

    export class PageTemplateComboBox extends api.ui.selector.combobox.RichComboBox<PageTemplate> {

        constructor() {
            super(new api.ui.selector.combobox.RichComboBoxBuilder<PageTemplate>().
                setSelectedOptionsView(new PageTemplateSelectedOptionsView()).
                setIdentifierMethod("getKey").
                setMaximumOccurrences(1).
                setOptionDisplayValueViewer(new PageTemplateViewer));
        }
    }

    export class PageTemplateSelectedOptionsView extends api.ui.selector.combobox.BaseSelectedOptionsView<PageTemplate> {

        createSelectedOption(option: api.ui.selector.Option<PageTemplate>): api.ui.selector.combobox.SelectedOption<PageTemplate> {
            return new api.ui.selector.combobox.SelectedOption<PageTemplate>(new PageTemplateSelectedOptionView(option), this.count());
        }
    }

    export class PageTemplateSelectedOptionView extends api.ui.selector.combobox.BaseSelectedOptionView<PageTemplate> {

        private pageTemplate: PageTemplate;

        constructor(option: api.ui.selector.Option<PageTemplate>) {
            this.pageTemplate = option.displayValue;
            super(option);
            this.addClass("page-template-selected-option-view");
        }

        layout() {
            var namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small).build();
            namesAndIconView.setIconClass("icon-newspaper icon-large")
                .setMainName(this.pageTemplate.getDisplayName())
                .setSubName(this.pageTemplate.getController().toString());

            var removeButtonEl = new api.dom.AEl("remove");
            removeButtonEl.onClicked((event: MouseEvent) => {
                this.notifyRemoveClicked();

                event.stopPropagation();
                event.preventDefault();
                return false;
            });

            this.appendChild(removeButtonEl);
            this.appendChild(namesAndIconView);
        }

    }
}