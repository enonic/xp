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

    export class TextComponentView extends PageComponentView<TextComponent> implements api.ui.text.TextEditorEditableArea {

        private textComponent: TextComponent;

        private textPlaceholder: TextPlaceholder;

        private editing: boolean;

        constructor(builder: TextComponentViewBuilder) {
            this.editing = false;
            this.textPlaceholder = new TextPlaceholder(this);
            super(builder.
                setContextMenuActions(this.createTextContextMenuActions()).
                setPlaceholder(this.textPlaceholder));
            this.textComponent = builder.pageComponent;

            if (this.conditionedForEmpty()) {
                this.markAsEmpty();
                this.addPlaceholder();
            }
            this.onKeyDown(this.handleKeyboard.bind(this));
            this.onKeyUp(this.handleKeyboard.bind(this));
            this.onDblClicked(this.handleDbClick.bind(this));
        }

        getElement(): api.dom.Element {
            return this;
        }

        processChanges() {
            this.textComponent.setText(this.getEl().getInnerHtml());
            new TextComponentEditedEvent(this).fire();
        }

        addPlaceholder() {
            this.removeChildren();
            this.appendChild(this.textPlaceholder);
        }

        removePlaceholder() {
            this.textPlaceholder.remove();
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

        handleDbClick(event: MouseEvent) {
            event.stopPropagation();
            event.preventDefault();
            if (this.editing) {
                this.showEditor();
                this.setCaretOffset(true);
            }
        }

        handleClick(event: MouseEvent) {
            event.stopPropagation();
            if (!this.isSelected()) {
                this.deselectParent();
                this.select(!this.isEmpty() ? { x: event.pageX, y: event.pageY } : null);
                this.makeEditable();
            } else if (!this.editing) {
                this.showEditor();
            }
        }

        handleKeyboard() {
            if (this.editing) {
                this.processChanges();
            }
        }

        select(clickPosition?: Position) {
            super.select(clickPosition);
            this.getEl().setCursor('url(' + api.util.getAdminUri('live-edit/images/pencil.png') + ') 0 40, text');
        }

        selectPlaceholder() {
            this.addPlaceholder();
        }

        deselect() {
            super.deselect();

            if (!this.isEmpty() && api.util.StringHelper.isBlank(this.getEl().getText())) {
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

        makeEditable() {
            var editableElement = this.getElement();
            editableElement.addClass('text-editor-editable-area').giveFocus();
            editableElement.getEl().setAttribute('contenteditable', 'true');
            new TextComponentStartEditingEvent(this).fire();
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

        private setCaretOffset(isFullSelection: Boolean) {
            var element = this.getHTMLElement();
            if (isFullSelection) {
                var selection = window.getSelection();
                var range = document.createRange();
                range.selectNodeContents(element);
                range.setStart(range.endContainer, range.startOffset);
                range.setEnd(range.endContainer, range.endOffset);
                selection.removeAllRanges();
                selection.addRange(range);
            } else {
                element.focus();
                element.click();
            }
        }

        private createTextContextMenuActions(): api.ui.Action[] {
            var actions: api.ui.Action[] = [];
            actions.push(new api.ui.Action('Edit').onExecuted(() => {
                if (!this.editing) {
                    this.showEditor();
                    this.setCaretOffset(false);
                }
            }));
            return actions;
        }
    }
}