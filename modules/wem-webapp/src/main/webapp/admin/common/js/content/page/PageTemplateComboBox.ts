module api.content.page {

    export class PageTemplateComboBox extends api.ui.selector.combobox.RichComboBox<PageTemplateSummary> {

        constructor() {
            super(new api.ui.selector.combobox.RichComboBoxBuilder<PageTemplateSummary>().
                setSelectedOptionsView(new PageTemplateSelectedOptionsView()).
                setIdentifierMethod("getKey").
                setMaximumOccurrences(1).
                setOptionDisplayValueViewer(new PageTemplateSummaryViewer));
        }
    }

    export class PageTemplateSelectedOptionsView extends api.ui.selector.combobox.SelectedOptionsView<PageTemplateSummary> {

        createSelectedOption(option: api.ui.selector.Option<PageTemplateSummary>,
                             index: number): api.ui.selector.combobox.SelectedOption<PageTemplateSummary> {
            return new api.ui.selector.combobox.SelectedOption<PageTemplateSummary>(new PageTemplateSelectedOptionView(option), option,
                index);
        }
    }

    export class PageTemplateSelectedOptionView extends api.ui.selector.combobox.SelectedOptionView<PageTemplateSummary> {

        private pageTemplate: PageTemplateSummary;

        constructor(option: api.ui.selector.Option<PageTemplateSummary>) {
            this.pageTemplate = option.displayValue;
            super(option);
            this.addClass("page-template-selected-option-view");
        }

        layout() {
            var namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize( api.app.NamesAndIconViewSize.small ).build();
            namesAndIconView.setIconClass( "icon-newspaper icon-large" )
                .setMainName( this.pageTemplate.getDisplayName() )
                .setSubName( this.pageTemplate.getDescriptorKey().toString() );

            var removeButtonEl = new api.dom.AEl("remove");
            removeButtonEl.onClicked((event: MouseEvent) => {
                this.notifySelectedOptionToBeRemoved();

                event.stopPropagation();
                event.preventDefault();
                return false;
            });

            this.appendChild(removeButtonEl);
            this.appendChild(namesAndIconView);
        }

    }
}