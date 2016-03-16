module api.liveedit.fragment {

    import ComponentView = api.liveedit.ComponentView;
    import ContentView = api.liveedit.ContentView;
    import RegionView = api.liveedit.RegionView;
    import FragmentComponent = api.content.page.region.FragmentComponent;
    import GetContentByIdRequest = api.content.GetContentByIdRequest;
    import Content = api.content.Content;

    export class FragmentComponentViewBuilder extends ComponentViewBuilder<FragmentComponent> {

        constructor() {
            super();
            this.setType(FragmentItemType.get());
        }
    }

    export class FragmentComponentView extends ComponentView<FragmentComponent> {

        private fragmentComponent: FragmentComponent;

        constructor(builder: FragmentComponentViewBuilder) {
            this.liveEditModel = builder.parentRegionView.getLiveEditModel();
            this.fragmentComponent = builder.component;

            super(builder.setPlaceholder(new FragmentPlaceholder(this)).setTooltipViewer(
                new FragmentComponentViewer()).setInspectActionRequired(true));

            this.parseContentViews(this);
        }

        isEmpty(): boolean {
            return !this.fragmentComponent || this.fragmentComponent.isEmpty();
        }

        protected getComponentContextMenuActions(actions: api.ui.Action[], liveEditModel: LiveEditModel): api.ui.Action[] {
            actions.push(new api.ui.Action("Edit in new tab").onExecuted(() => {
                this.deselect();
                new GetContentByIdRequest(this.fragmentComponent.getFragment()).sendAndParse().then((content: Content)=> {
                    var contentAndSummary = api.content.ContentSummaryAndCompareStatus.fromContentSummary(content);
                    new api.content.event.EditContentEvent([contentAndSummary]).fire();
                });
            }));
            return actions;
        }

        private parseContentViews(parentElement?: api.dom.Element) {
            // remove component-type attributes to avoid inner components of fragment to be affected by d&d sorting
            var children = parentElement.getChildren();
            children.forEach((childElement: api.dom.Element) => {
                var itemType = ItemType.fromElement(childElement);
                if (itemType) {
                    childElement.getHTMLElement().removeAttribute("data-" + ItemType.ATTRIBUTE_TYPE);
                }
                this.parseContentViews(childElement);
            });
        }
    }
}