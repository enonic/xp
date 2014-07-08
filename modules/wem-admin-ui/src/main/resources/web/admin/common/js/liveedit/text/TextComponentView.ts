module api.liveedit.text {

    import PageComponentView = api.liveedit.PageComponentView;
    import RegionView = api.liveedit.RegionView;
    import TextComponent = api.content.page.text.TextComponent;

    export class TextComponentViewBuilder extends PageComponentViewBuilder<TextComponent> {

        constructor() {
            super();
            this.setType(TextItemType.get());
        }
    }

    export class TextComponentView extends PageComponentView<TextComponent> {

        private textComponent: TextComponent;

        private placeholder: TextPlaceholder;

        private editArea: api.dom.DivEl;

        private editing: boolean;

        private editedListener: {(): void}[];

        constructor(builder: TextComponentViewBuilder) {
            this.editedListener = [];

            super(builder.setContextMenuActions(this.createTextContextMenuActions()));
            this.textComponent = builder.pageComponent;
            this.placeholder = new TextPlaceholder();
            this.placeholder.hide();
            this.appendChild(this.placeholder);

            this.editArea = new api.dom.DivEl('live-edit-edited-area');
            this.editArea.getEl().setCursor('text');
            this.editArea.hide();
            this.appendChild(this.editArea);

            if (api.util.isStringBlank(this.textComponent.getText())) {
                this.markAsEmpty();
            } else {
                this.editArea.getEl().setInnerHtml(this.textComponent.getText());
                this.editArea.show();
            }

            this.placeholder.onClicked((event: MouseEvent) => {
                this.editing = true;

                this.placeholder.hide();

                this.editArea.getEl().setInnerHtml('</br>');
                this.editArea.show();

                this.removeEmptyMark();
                super.select();

                api.ui.text.TextEditor.get().showToolbar(this.editArea);
                this.notifyEdited();
            });

            this.editArea.onClicked((event: MouseEvent) => {
                if (this.isSelected()) {
                    if (!this.editing) {
                        this.editing = true;
                        api.ui.text.TextEditor.get().showToolbar(this.editArea);
                    }
                    this.notifyEdited();
                }
            });

            this.editArea.onKeyDown(this.notifyEdited.bind(this));
            this.editArea.onKeyUp(this.notifyEdited.bind(this));
        }

        duplicate(duplicate: TextComponent): TextComponentView {

            var duplicatedView = new TextComponentView(new TextComponentViewBuilder().
                setParentRegionView(this.getParentItemView()).
                setParentElement(this.getParentElement()).
                setPageComponent(duplicate));
            duplicatedView.insertAfterEl(this);
            return duplicatedView;
        }

        select(clickPosition?: Position) {
            super.select(clickPosition);

            if (this.isEmpty()) {
                this.placeholder.show();
            }

            this.getEl().setCursor('url(../../admin/live-edit/images/pencil.png) 0 40, text');
        }

        deselect() {
            super.deselect();
            if (!this.isEmpty() && api.util.isStringBlank(this.editArea.getEl().getText())) {
                this.markAsEmpty();
            }
            this.placeholder.hide();
            if (this.editing) {
                api.ui.text.TextEditor.get().hideToolbar();
            }
            this.editing = false;

            this.getEl().setCursor('');
        }

        public static fromJQuery(element: JQuery): TextComponentView {
            return new TextComponentView(new TextComponentViewBuilder().setElement(api.dom.Element.fromHtmlElement(<HTMLElement>element.get(0))));
        }

        getTooltipViewer(): TextComponentViewer {
            return new TextComponentViewer();
        }

        onEdited(listener: () => void) {
            if (!this.editedListener) {
                this.editedListener = [];
            }
            this.editedListener.push(listener);
        }

        unEdited(listener: () => void) {
            this.editedListener = this.editedListener.filter((current) => (current != listener));
        }

        private notifyEdited() {
            this.editedListener.forEach((listener: () => void) => listener());
        }

        private createTextContextMenuActions(): api.ui.Action[] {
            var actions: api.ui.Action[] = [];
            actions.push(new api.ui.Action('Edit').onExecuted(() => {
                // TODO
            }));
            return actions;
        }
    }
}