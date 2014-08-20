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

        private placeholder: api.dom.DivEl;

        private editing: boolean;

        private oneTimeCaretFlag: boolean;

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

        handleDbClick(event: MouseEvent) {
            event.stopPropagation();
            event.preventDefault();

            if (!this.isSelected()) {
                this.deselectParent();
                this.select(!this.isEmpty() ? { x: event.pageX, y: event.pageY } : null);
            } else if (this.editing) {
                this.showEditor("full");
            }
        }

        private timer;
        public newEvent: MouseEvent;
        public elem: HTMLElement;

        handleClick(event: MouseEvent) {
            event.stopPropagation();

            if (!this.isSelected()) {
                this.deselectParent();
                this.select(!this.isEmpty() ? { x: event.pageX, y: event.pageY } : null);
            } else if (!this.editing) {
                event.stopPropagation();

                this.showEditor("end");
                var newEvent: MouseEvent;

                newEvent = <MouseEvent> document.createEvent("MouseEvents");


                newEvent.initMouseEvent(event.type, true, event.cancelable, event.view, event.detail, event.screenX, event.screenY,
                    event.clientX, event.clientY,
                    event.ctrlKey, event.altKey, event.shiftKey, event.metaKey, event.button, event.relatedTarget);

                 newEvent.currentTarget=this.getHTMLElement();
                newEvent.srcElement = this.getHTMLElement();
                 newEvent.offsetX = event.offsetX;
                 newEvent.offsetY = event.offsetY;
                 newEvent.toElement = this.getHTMLElement();
                 newEvent.target=this.getHTMLElement();


                var element = this.getHTMLElement();
                element.focus();
                this.newEvent = newEvent;

                this.timer = setTimeout(() =>
                    element.dispatchEvent(this.newEvent), 2000);
            }
        }


        handleKeyboard() {
            if (this.editing) {
                this.processChanges();
            }
        }

        select(clickPosition?: Position) {
            super.select(clickPosition);

            if (this.isEmpty()) {
                this.addPlaceholder();
            }

            this.getEl().setCursor('url(' + api.util.getAdminUri('live-edit/images/pencil.png') + ') 0 40, text');
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

        showEditor(caretPosition) {
            if (this.isEmpty()) {
                this.removeEmptyMark();
                this.removePlaceholder();
                this.getEl().setInnerHtml('</br>');
            }

            this.setCaretOffset(caretPosition);
            this.editing = true;
            api.ui.text.TextEditorToolbar.get().showToolbar(this);

            this.hideTooltip();
            this.hideContextMenu();
            new TextComponentStartEditingEvent(this).fire();
        }

        getTooltipViewer(): TextComponentViewer {
            return new TextComponentViewer();
        }

        private setCaretOffset(caretPosition) {
            var element = this.getHTMLElement();
            var selection = window.getSelection();
            var range = document.createRange();


            /*    if (this.oneTimeCaretFlag) {
             this.oneTimeCaretFlag = false;
                return;
             }*/

            range.selectNodeContents(element);
            if (caretPosition == "start") {
                element.focus();
                element.click();
            } else if (caretPosition == "end") {

            } else if (caretPosition == "full") {
                range.setStart(range.endContainer, range.startOffset);
                range.setEnd(range.endContainer, range.endOffset);
                selection.removeAllRanges();
                selection.addRange(range);
                //  this.oneTimeCaretFlag = true;

            }

            /* element.click();
             selection.removeAllRanges();
             selection.addRange(range);
             this.oneTimeCaretFlag = true;*/

        }

        private createTextContextMenuActions(): api.ui.Action[] {
            var actions: api.ui.Action[] = [];
            actions.push(new api.ui.Action('Edit').onExecuted(() => {
                if (!this.editing) {
                    this.showEditor("start");
                }
            }));
            return actions;
        }
    }
}