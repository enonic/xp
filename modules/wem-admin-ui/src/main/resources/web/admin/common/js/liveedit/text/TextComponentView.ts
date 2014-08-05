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

        private editing: boolean;

        constructor(builder: TextComponentViewBuilder) {
            this.editing = false;
            super(builder.setContextMenuActions(this.createTextContextMenuActions()));
            this.textComponent = builder.pageComponent;

            this.placeholder = new api.dom.DivEl('text-placeholder');
            this.placeholder.getEl().setInnerHtml('Click to edit');
            if (this.conditionedForEmpty()) {
                this.markAsEmpty();
                this.addPlaceholder();
            }

            this.onKeyDown(this.handleKeyboard.bind(this));
            this.onKeyUp(this.handleKeyboard.bind(this));
        }

        addPlaceholder() {
            this.removeChildren();
            this.appendChild(this.placeholder);
        }

        removePlaceholder() {
            this.placeholder.remove();
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

        handleKeyboard() {
            if (this.editing) {
                this.textComponent.setText(this.getEl().getInnerHtml());
                new TextComponentEditedEvent(this).fire();
            }
        }

        select(clickPosition?: Position) {
            super.select(clickPosition);

            if (this.isEmpty()) {
                this.addPlaceholder();
                this.getEl().setCursor('url(' + api.util.getAdminUri('live-edit/images/pencil.png') + ') 0 40, text');
            } else {
                this.hideContextMenu();
                this.showEditor();
            }
        }

        deselect() {
            super.deselect();

            if (this.isEmpty()) {
                this.removePlaceholder();
            } else if (api.util.isStringBlank(this.getEl().getText())) {
                this.markAsEmpty();
            }

            if (this.editing) {
                api.ui.text.TextEditorToolbar.get().hideToolbar();
                this.editing = false;
            }

            this.getEl().setCursor('');
        }

        conditionedForEmpty(): boolean {
            if (!this.textComponent) {
                return this.isEmpty();
            }
            return this.isEmpty() || !this.textComponent.getText();
        }

        showEditor() {
            if (this.isEmpty()) {
                this.removeEmptyMark();
                this.removePlaceholder();
                this.getEl().setInnerHtml('</br>');
            }

            this.editing = true;
            api.ui.text.TextEditorToolbar.get().showToolbar(this);

            this.hideTooltip();
            this.hideContextMenu();
            new TextComponentStartEditingEvent(this).fire();
        }

        getTooltipViewer(): TextComponentViewer {
            return new TextComponentViewer();
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