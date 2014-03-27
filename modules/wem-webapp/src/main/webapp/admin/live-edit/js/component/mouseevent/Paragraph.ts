module LiveEdit.component.mouseevent {

    // Uses
    var $ = $liveEdit;

    enum ParagraphMode {
        UNSELECTED,
        SELECTED,
        EDIT
    }

    export class Paragraph extends LiveEdit.component.mouseevent.Base {

        private selectedParagraph: LiveEdit.component.Component = null;
        private modes: any = {};
        private currentMode: ParagraphMode;

        constructor() {
            super();

            this.componentCssSelectorFilter = LiveEdit.component.TypeConfiguration[LiveEdit.component.Type.PARAGRAPH].cssSelector;

            this.currentMode = ParagraphMode.UNSELECTED;

            this.attachMouseOverEvent();

            this.attachMouseOutEvent();

            this.attachClickEvent();

            this.registerGlobalListeners();
        }

        registerGlobalListeners(): void {
            $(window).on('clickShader.liveEdit deselectComponent.liveEdit', () => {
                this.leaveEditMode();
            });
        }

        // Override base attachClickEvent
        attachClickEvent(): void {
            // Listen for left/right click.
            $(document).on('click contextmenu touchstart', this.componentCssSelectorFilter, (event: JQueryEventObject) => {
                var paragraphComponent = LiveEdit.component.Component.fromJQuery($(event.currentTarget));
                this.handleClick(event, paragraphComponent);
            });
        }

        handleClick(event: JQueryEventObject, component: LiveEdit.component.Component): void {
            event.stopPropagation();
            event.preventDefault();

            // Remove the inlined css cursor when the mode is not EDIT.
            if (this.selectedParagraph && !(this.currentMode === ParagraphMode.EDIT)) {
                this.selectedParagraph.getElement().css('cursor', '');
            }

            // Reset mode in case another paragraph is selected.
            if (this.selectedParagraph && !component.getElement().is(this.selectedParagraph.getElement())) {
                this.currentMode = ParagraphMode.UNSELECTED;
            }

            this.selectedParagraph = component;

            if (this.currentMode === ParagraphMode.UNSELECTED) {
                this.setSelectMode(event);
            } else if (this.currentMode === ParagraphMode.SELECTED) {
                this.setEditMode();
            } else {
                /**/
            }
        }

        setSelectMode(event: JQueryEventObject): void {

            this.selectedParagraph.getElement().css('cursor', 'url(../../admin/live-edit/images/pencil.png) 0 40, text');
            this.currentMode = ParagraphMode.SELECTED;

            // Make sure Chrome does not selects the text on context click
            if (window.getSelection) {
                window.getSelection().removeAllRanges();
            }

            LiveEdit.component.Selection.removeSelectedAttribute();

            LiveEdit.component.Selection.handleSelect(this.selectedParagraph.getHTMLElement(), event);

            $(window).trigger('selectParagraphComponent.liveEdit', [this.selectedParagraph]);
        }

        setEditMode(): void {

            var paragraphComponent = this.selectedParagraph;

//            paragraphComponent.onResized((event: api.dom.ElementResizedEvent) => {
//                console.log('resize' , event);
//                $(window).trigger('editParagraphComponent.liveEdit', [paragraphComponent]);
//            });

            $(window).trigger('editParagraphComponent.liveEdit', [this.selectedParagraph]);

            paragraphComponent.getElement().css('cursor', 'text');
            paragraphComponent.getElement().addClass('live-edit-edited-paragraph');
            paragraphComponent.getElement().removeClass('live-edit-empty-component');

            this.currentMode = ParagraphMode.EDIT;
        }

        leaveEditMode(): void {
            var paragraphComponent = this.selectedParagraph;
            if (paragraphComponent === null) {
                return;
            }

            $(window).trigger('leaveParagraphComponent.liveEdit', [this.selectedParagraph]);

            paragraphComponent.getElement().css('cursor', '');
            paragraphComponent.getElement().removeClass('live-edit-edited-paragraph');

            var isEmpty = !$.trim(paragraphComponent.getElement().html()).length;
            if (isEmpty) {
                paragraphComponent.getElement().addClass('live-edit-empty-component');
            }

            this.selectedParagraph = null;

            this.currentMode = ParagraphMode.UNSELECTED;

            LiveEdit.component.Selection.removeSelectedAttribute();

        }
    }
}
