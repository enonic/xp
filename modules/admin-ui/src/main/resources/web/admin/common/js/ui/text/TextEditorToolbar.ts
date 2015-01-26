module api.ui.text {

    export class TextEditorToolbar extends api.dom.DivEl {

        private static instance: TextEditorToolbar = null;

        private static COMMANDS: string[] = [
            "paste", "insertUnorderedList", "insertOrderedList", "link", "cut", "strikeThrough", "bold", "underline", "italic",
            "superscript", "subscript", "justifyLeft", "justifyCenter", "justifyRight", "justifyFull"
        ];

        private editArea: TextEditorEditableArea;

        static get(): TextEditorToolbar {
            if (!this.instance) {
                this.instance = new TextEditorToolbar();
            }
            return this.instance;
        }

        constructor() {
            super("text-editor-toolbar");
            this.getEl().setDisplay('none');

            TextEditorToolbar.COMMANDS.forEach((name: string, index: number) => {
                var button = new api.dom.ButtonEl("text-editor-button");
                button.getEl().setAttribute("data-text-editor-tag", name);
                this.appendChild(button);
            });

            api.dom.Body.get().appendChild(this);

            this.onClicked((event: MouseEvent) => {
                event.stopPropagation();
                var tag = event.target["getAttribute"]('data-text-editor-tag');
                if (tag) {
                    document.execCommand(tag, false, null);
                    this.editArea.processChanges();
                }
            });

            api.dom.WindowDOM.get().onScroll(() => {
                if (this.editArea) {
                    this.updatePosition();
                }
            });
        }

        showToolbar(editEl: TextEditorEditableArea) {
            this.editArea = editEl;

            this.updatePosition();
            this.show();
            this.toggleArrowPosition(false);
        }

        hideToolbar() {

            this.editArea = null;
            this.hide();
        }

        private toggleArrowPosition(showArrowAtTop: boolean): void {
            if (showArrowAtTop) {
                this.getEl().removeClass('top').addClass('bottom');
            } else {
                this.getEl().removeClass('bottom').addClass('top');
            }
        }

        private updatePosition() {
            if (!this.editArea) {
                return;
            }

            var defaultPosition = this.getPositionRelativeToComponentTop();

            var stick = api.dom.WindowDOM.get().getScrollTop() >= this.editArea.getElement().getEl().getOffsetTop() - 60;

            var el = this.getEl();

            if (stick) {
                el.setPosition('fixed');
                el.setTopPx(10);
                el.setLeftPx(defaultPosition.left);
            } else {
                el.setPosition('absolute');
                el.setTopPx(defaultPosition.top);
                el.setLeftPx(defaultPosition.left);
            }

            var placeArrowOnTop = api.dom.WindowDOM.get().getScrollTop() >= defaultPosition.bottom - 10;

            this.toggleArrowPosition(placeArrowOnTop);
        }

        private getPositionRelativeToComponentTop(): any {
            var dimensions = this.getDimensions(this.editArea.getElement()),
                leftPos = dimensions.left + (dimensions.width / 2 - this.getEl().getWidthWithBorder() / 2),
                topPos = dimensions.top - this.getEl().getHeightWithBorder() - 25;

            return {
                left: leftPos,
                top: topPos,
                bottom: dimensions.top + dimensions.height
            };
        }

        private getDimensions(element: api.dom.Element): api.liveedit.ElementDimensions {
            var editAreaEl = element.getEl();
            return {
                top: editAreaEl.getOffsetTop(),
                left: editAreaEl.getOffsetLeft(),
                width: editAreaEl.getWidthWithBorder(),
                height: editAreaEl.getHeightWithBorder()
            }
        }

    }

}