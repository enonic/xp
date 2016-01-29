module api.liveedit.fragment {

    import ContentTypeName = api.schema.content.ContentTypeName;
    import FragmentComponent = api.content.page.region.FragmentComponent;

    export class FragmentPlaceholder extends api.liveedit.ItemViewPlaceholder {

        private fragmentComponentView: FragmentComponentView;

        private comboBox: api.content.ContentComboBox;

        private comboboxWrapper: api.dom.DivEl;

        constructor(fragmentView: FragmentComponentView) {
            super();
            this.addClassEx("fragment-placeholder");
            this.fragmentComponentView = fragmentView;

            this.comboboxWrapper = new api.dom.DivEl('rich-combobox-wrapper');

            var loader = new api.content.ContentSummaryLoader();
            loader.setAllowedContentTypeNames([ContentTypeName.FRAGMENT]);

            this.comboBox = api.content.ContentComboBox.create().setMaximumOccurrences(1).setLoader(loader).setMinWidth(270).build();

            this.comboboxWrapper.appendChildren(this.comboBox);
            this.appendChild(this.comboboxWrapper);

            this.comboBox.onOptionSelected((selectedOption: api.ui.selector.combobox.SelectedOption<api.content.ContentSummary>) => {

                var component: FragmentComponent = this.fragmentComponentView.getComponent();
                var fragmentContent = selectedOption.getOption().displayValue;

                component.setFragment(fragmentContent.getContentId(), fragmentContent.getDisplayName());

                this.fragmentComponentView.showLoadingSpinner();
            });
        }

        select() {
            this.comboboxWrapper.show();
            this.comboBox.giveFocus();
        }

        deselect() {
            this.comboboxWrapper.hide();
        }
    }
}