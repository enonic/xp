module api.liveedit.fragment {

    import ContentTypeName = api.schema.content.ContentTypeName;
    import FragmentComponent = api.content.page.region.FragmentComponent;
    import GetContentByIdRequest = api.content.GetContentByIdRequest;
    import Content = api.content.Content;
    import LayoutComponentType = api.content.page.region.LayoutComponentType;
    import QueryExpr = api.query.expr.QueryExpr;
    import CompareExpr = api.query.expr.CompareExpr;
    import FieldExpr = api.query.expr.FieldExpr;
    import ValueExpr = api.query.expr.ValueExpr;

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
            loader.setQueryExpr(this.createParentSiteFragmentsOnlyQuery());

            this.comboBox = api.content.ContentComboBox.create().setMaximumOccurrences(1).setLoader(loader).setMinWidth(270).build();

            this.comboboxWrapper.appendChildren(this.comboBox);
            this.appendChild(this.comboboxWrapper);

            this.comboBox.onOptionSelected((selectedOption: api.ui.selector.combobox.SelectedOption<api.content.ContentSummary>) => {

                var component: FragmentComponent = this.fragmentComponentView.getComponent();
                var fragmentContent = selectedOption.getOption().displayValue;

                if (this.isInsideLayout()) {
                    new GetContentByIdRequest(fragmentContent.getContentId()).sendAndParse().done((content: Content) => {
                        let fragmentComponent = content.getPage() ? content.getPage().getFragment() : null;
                        
                        if (fragmentComponent && api.ObjectHelper.iFrameSafeInstanceOf(fragmentComponent.getType(), LayoutComponentType)) {
                            this.comboBox.clearSelection();
                            new api.liveedit.ShowWarningLiveEditEvent("Layout within layout not allowed").fire();
                            
                        } else {
                            component.setFragment(fragmentContent.getContentId(), fragmentContent.getDisplayName());
                            this.fragmentComponentView.showLoadingSpinner();
                        }
                    });
                } else {
                    component.setFragment(fragmentContent.getContentId(), fragmentContent.getDisplayName());
                    this.fragmentComponentView.showLoadingSpinner();
                }
            });
        }

        private isInsideLayout(): boolean {
            let parentRegion = this.fragmentComponentView.getParentItemView();
            if (!parentRegion) {
                return false;
            }
            let parent = parentRegion.getParentItemView();
            if (!parent) {
                return false;
            }
            return api.ObjectHelper.iFrameSafeInstanceOf(parent.getType(), api.liveedit.layout.LayoutItemType);
        }

        private createParentSiteFragmentsOnlyQuery(): QueryExpr {
            var sitePath = this.fragmentComponentView.getLiveEditModel().getSiteModel().getSite().getPath().toString();
            var compareExpr: CompareExpr = CompareExpr.like(new FieldExpr("_path"), ValueExpr.string("/content" + sitePath + "/*" ));
            return new QueryExpr(compareExpr);
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