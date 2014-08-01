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

        private placeholder: api.dom.DivEl;

        private editArea: api.dom.DivEl;

        private editing: boolean;

        private editedListener: {(): void}[];

        constructor(builder: TextComponentViewBuilder) {
            this.editedListener = [];

            super(builder.setContextMenuActions(this.createTextContextMenuActions()));
            this.textComponent = builder.pageComponent;

            this.placeholder = new api.dom.DivEl('text-placeholder');
            this.placeholder.getEl().setInnerHtml('Click to edit');
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

            this.editArea.onKeyDown(this.notifyEdited.bind(this));
            this.editArea.onKeyUp(this.notifyEdited.bind(this));
        }

        duplicate(duplicate: TextComponent): TextComponentView {

            var duplicatedView = new TextComponentView(new TextComponentViewBuilder().
                setParentRegionView(this.getParentItemView()).
                setParentElement(this.getParentElement()).
                setPageComponent(duplicate));
            duplicatedView.insertAfterEl(this);
            duplicatedView.displayPlaceholder();
            return duplicatedView;
        }

        handleClick(event: MouseEvent) {
            event.stopPropagation();
            event.preventDefault();

            if (!this.isSelected()) {
                this.select(!this.isEmpty() ? { x: event.pageX, y: event.pageY } : null);
            } else if (!this.editing) {
                this.showEditor();
            }
        }

        select(clickPosition?: Position) {
            super.select(clickPosition);

            if (this.isEmpty()) {
                this.placeholder.show();
            }

            this.getEl().setCursor('url(' + api.util.getAdminUri('live-edit/images/pencil.png') + ') 0 40, text');
        }

        deselect() {
            super.deselect();

            if (this.isEmpty()) {
                this.placeholder.hide();
            } else if (api.util.isStringBlank(this.editArea.getEl().getText())) {
                this.markAsEmpty();
            }

            if (this.editing) {
                api.ui.text.TextEditorToolbar.get().hideToolbar();
                var text = this.editArea.getEl().getInnerHtml();
                this.textComponent.setText(text);
                this.editing = false;
            }

            this.getEl().setCursor('');
        }

        showEditor() {
            if (this.isEmpty()) {
                this.removeEmptyMark();
                this.placeholder.hide();
                this.editArea.getEl().setInnerHtml('</br>');
                this.editArea.show();
            }

            this.editing = true;
            api.ui.text.TextEditorToolbar.get().showToolbar(this.editArea);

            this.notifyEdited();
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
                if (!this.editing) {
                    this.showEditor();
                }
            }));
            return actions;
        }
    }
}