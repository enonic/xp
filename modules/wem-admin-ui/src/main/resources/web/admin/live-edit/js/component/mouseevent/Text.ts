module LiveEdit.component.mouseevent {

    import TextItemType = api.liveedit.text.TextItemType;
    import TextView = api.liveedit.text.TextView;
    import PageComponentDeselectEvent = api.liveedit.PageComponentDeselectEvent;

    enum TextMode {
        UNSELECTED,
        SELECTED,
        EDIT
    }

    export class Text extends LiveEdit.component.mouseevent.Base {

        private selectedText: TextView = null;
        private modes: any = {};
        private currentMode: TextMode;

        constructor() {
            super();

            this.componentCssSelectorFilter = TextItemType.get().getConfig().getCssSelector();

            this.currentMode = TextMode.UNSELECTED;

            this.attachMouseOverEvent();

            this.attachMouseOutEvent();

            this.attachClickEvent();

            this.registerGlobalListeners();
        }

        registerGlobalListeners(): void {
            wemjq(window).on('clickShader.liveEdit', () => this.leaveEditMode());

            PageComponentDeselectEvent.on(() => this.leaveEditMode());
        }

        // Override base attachClickEvent
        attachClickEvent(): void {
            // Listen for left/right click.
            wemjq(document).on('click contextmenu touchstart', this.componentCssSelectorFilter, (event: JQueryEventObject) => {
                var textComponent = TextView.fromJQuery(wemjq(event.currentTarget));
                this.handleClick(event, textComponent);
            });
        }

        handleClick(event: JQueryEventObject, component: TextView): void {
            event.stopPropagation();
            event.preventDefault();

            // Remove the inlined css cursor when the mode is not EDIT.
            if (this.selectedText && !(this.currentMode === TextMode.EDIT)) {
                this.selectedText.getElement().css('cursor', '');
            }

            // Reset mode in case another text is selected.
            if (this.selectedText && !component.getElement().is(this.selectedText.getElement())) {
                this.currentMode = TextMode.UNSELECTED;
            }

            this.selectedText = component;

            if (this.currentMode === TextMode.UNSELECTED) {
                this.setSelectMode(event);
            } else if (this.currentMode === TextMode.SELECTED) {
                this.setEditMode();
            } else {
                /**/
            }
        }

        setSelectMode(event: JQueryEventObject): void {

            if (!this.isSelectedTextBlankOrEmpty()) {
                this.selectedText.getElement().css('cursor', 'url(../../admin/live-edit/images/pencil.png) 0 40, text');
            }
            this.currentMode = TextMode.SELECTED;

            // Make sure Chrome does not selects the text on context click
            if (window.getSelection) {
                window.getSelection().removeAllRanges();
            }

            LiveEdit.LiveEditPage.get().deselectSelectedView();

            this.selectedText.select(event);

            wemjq(window).trigger('selectTextComponent.liveEdit', [this.selectedText]);
        }

        setEditMode(): void {

            var textComponent = this.selectedText;
            wemjq('.text-link-click-to-edit').remove();
//            textComponent.onResized((event: api.dom.ElementResizedEvent) => {
//                console.log('resize' , event);
//                wemjq(window).trigger('editTextComponent.liveEdit', [textComponent]);
//            });

            if (this.isSelectedTextEmpty()) {
                textComponent.appendChild(new api.dom.BrEl());
            }
            textComponent.getElement().on('keydown keyup', (event) => {
                wemjq(window).trigger('editTextComponent.liveEdit', [textComponent]);
            });

            wemjq(window).trigger('editTextComponent.liveEdit', [this.selectedText]);

            textComponent.getElement().css('cursor', 'text');
            textComponent.getElement().addClass('live-edit-edited-text');
            textComponent.getElement().removeClass('live-edit-empty-component');

            wemjq(window).trigger('editTextComponent.liveEdit', [textComponent]);

            this.currentMode = TextMode.EDIT;
        }

        leaveEditMode(): void {
            var textComponent = this.selectedText;
            if (textComponent === null) {
                return;
            }

            wemjq(window).trigger('leaveTextComponent.liveEdit', [this.selectedText]);

            textComponent.getElement().css('cursor', '');
            textComponent.getElement().removeClass('live-edit-edited-text');

            if (this.isSelectedTextBlankOrEmpty()) {
                textComponent.getElement().addClass('live-edit-empty-component');
            }

            this.selectedText.deselect();
            this.selectedText = null;

            this.currentMode = TextMode.UNSELECTED;
        }

        private isSelectedTextBlankOrEmpty(): boolean {
            var textContent = wemjq.trim(this.selectedText.getElement().html());
            var isBlank = !textContent.length || ('<br>' === textContent);
            return isBlank;
        }

        private isSelectedTextEmpty(): boolean {
            return !wemjq.trim(this.selectedText.getElement().html()).length;
        }

    }
}
