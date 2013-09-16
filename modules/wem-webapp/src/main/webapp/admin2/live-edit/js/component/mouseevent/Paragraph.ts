module LiveEdit.component.mouseevent {

    // Uses
    var $ = $liveEdit;

    export class Paragraph extends LiveEdit.component.mouseevent.Base {

        private selectedParagraph:LiveEdit.component.Component = null;
        private modes:any = {};
        private currentMode:number;

        constructor() {
            super();

            this.componentCssSelectorFilter = LiveEdit.component.Configuration[LiveEdit.component.Type.PARAGRAPH].cssSelector;

            this.modes = {
                UNSELECTED: 0,
                SELECTED: 1,
                EDIT: 2
            };

            this.currentMode = this.modes.UNSELECTED;

            this.attachMouseOverEvent();

            this.attachMouseOutEvent();

            this.attachClickEvent();

            this.registerGlobalListeners();
        }

        registerGlobalListeners():void {
            $(window).on('clickShader.liveEdit deselectComponent.liveEdit', () => {
                this.leaveEditMode();
            });
        }

        // Override base attachClickEvent
        attachClickEvent():void {
            // Listen for left/right click.
            $(document).on('click contextmenu touchstart', this.componentCssSelectorFilter, (event:JQueryEventObject) => {
                var paragraphComponent = new LiveEdit.component.Component($(event.currentTarget));
                this.handleClick(event, paragraphComponent);
            });
        }

        handleClick(event:JQueryEventObject, component:LiveEdit.component.Component):void {
            event.stopPropagation();
            event.preventDefault();

            // Remove the inlined css cursor when the mode is not EDIT.
            if (this.selectedParagraph && !(this.currentMode === this.modes.EDIT)) {
                this.selectedParagraph.getElement().css('cursor', '');
            }

            // Reset mode in case another paragraph is selected.
            if (this.selectedParagraph && !component.getElement().is(this.selectedParagraph.getElement())) {
                this.currentMode = this.modes.UNSELECTED;
            }

            this.selectedParagraph = component;

            if (this.currentMode === this.modes.UNSELECTED) {
                this.setSelectMode(event);
            } else if (this.currentMode === this.modes.SELECTED) {
                this.setEditMode();
            } else {
                /**/
            }
        }

        setSelectMode(event:JQueryEventObject):void {
            console.log('Paragraph select mode');

            this.selectedParagraph.getElement().css('cursor', 'url(../../admin2/live-edit/images/pencil.png) 0 40, text');
            this.currentMode = this.modes.SELECTED;

            // Make sure Chrome does not selects the text on context click
            if (window.getSelection) {
                window.getSelection().removeAllRanges();
            }

            LiveEdit.Selection.clearSelection();

            LiveEdit.Selection.select(this.selectedParagraph, event);

            $(window).trigger('selectParagraphComponent.liveEdit', [this.selectedParagraph]);
        }

        setEditMode():void {
            console.log('Paragraph edit mode');

            var paragraphComponent = this.selectedParagraph;

            $(window).trigger('editParagraphComponent.liveEdit', [this.selectedParagraph]);

            paragraphComponent.getElement().css('cursor', 'text');
            paragraphComponent.getElement().addClass('live-edit-edited-paragraph');

            this.currentMode = this.modes.EDIT;
        }

        leaveEditMode():void {
            var paragraphComponent = this.selectedParagraph;
            if (paragraphComponent === null) {
                return;
            }

            console.log('Paragraph leave edit mode');

            $(window).trigger('leaveParagraphComponent.liveEdit', [this.selectedParagraph]);

            paragraphComponent.getElement().css('cursor', '');
            paragraphComponent.getElement().removeClass('live-edit-edited-paragraph');
            this.selectedParagraph = null;

            this.currentMode = this.modes.UNSELECTED;

            LiveEdit.Selection.clearSelection();

        }
    }
}
