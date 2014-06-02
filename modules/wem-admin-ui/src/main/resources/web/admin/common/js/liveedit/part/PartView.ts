module api.liveedit.part {

    import PageComponentView = api.liveedit.PageComponentView;
    import ContentView = api.liveedit.ContentView;
    import RegionView = api.liveedit.RegionView;

    export class PartView extends PageComponentView {

        private contentViews: ContentView[] = [];

        private placeholder: PartPlaceholder;

        constructor(element?: HTMLElement, dummy?: boolean) {
            super(PartItemType.get(), element, dummy);

            this.placeholder = new PartPlaceholder(this);
        }

        addContent(view: api.liveedit.ContentView) {
            this.contentViews.push(view);
        }

        showHighlighter(value: boolean) {


        }

        select() {
            super.select();
            if( this.isEmpty() ) {
                this.placeholder.select();
            }
        }

        deselect() {
            super.deselect();
            if( this.isEmpty() ) {
                this.placeholder.deselect();
            }
        }

        empty() {
            super.empty();

            this.removeChildren();
            this.appendChild(this.placeholder);

        }

        duplicate(): PartView {

            var duplicatedView = new PartView();
            this.getEl().insertAfterThisEl(duplicatedView.getEl());
            return duplicatedView;
        }
    }
}