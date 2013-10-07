module LiveEdit.ui {

    // Uses
    var $ = $liveEdit;

    export class EditorToolbar extends LiveEdit.ui.Base {

        private selectedParagraph:LiveEdit.component.Component = null;

        constructor() {
            super();

            this.selectedParagraph = null;

            this.addView();
            this.addEvents();
            this.registerGlobalListeners();
        }

        private registerGlobalListeners():void {
            $(window).on('editParagraphComponent.liveEdit', (event:JQueryEventObject, component) => this.show(component));
            $(window).on('leaveParagraphComponent.liveEdit', () => this.hide());
            $(window).on('componentRemoved.liveEdit', () => this.hide());
            $(window).on('sortableStart.liveEdit', () => this.hide());
        }

        private addView():void {
            var html:string = '<div class="live-edit-editor-toolbar live-edit-arrow-bottom" style="display: none">' +
                '    <button live-edit-data-tag="paste" class="live-edit-editor-button"></button>' +
                '    <button live-edit-data-tag="insertUnorderedList" class="live-edit-editor-button"></button>' +
                '    <button live-edit-data-tag="insertOrderedList" class="live-edit-editor-button"></button>' +
                '    <button live-edit-data-tag="link" class="live-edit-editor-button"></button>' +
                '    <button live-edit-data-tag="cut" class="live-edit-editor-button"></button>' +
                '    <button live-edit-data-tag="strikeThrough" class="live-edit-editor-button"></button>' +
                '    <button live-edit-data-tag="bold" class="live-edit-editor-button"></button>' +
                '    <button live-edit-data-tag="underline" class="live-edit-editor-button"></button>' +
                '    <button live-edit-data-tag="italic" class="live-edit-editor-button"></button>' +
                '    <button live-edit-data-tag="superscript" class="live-edit-editor-button"></button>' +
                '    <button live-edit-data-tag="subscript" class="live-edit-editor-button"></button>' +
                '    <button live-edit-data-tag="justifyLeft" class="live-edit-editor-button"></button>' +
                '    <button live-edit-data-tag="justifyCenter" class="live-edit-editor-button"></button>' +
                '    <button live-edit-data-tag="justifyRight" class="live-edit-editor-button"></button>' +
                '    <button live-edit-data-tag="justifyFull" class="live-edit-editor-button"></button>' +
                '</div>';

            this.createHtmlFromString(html);
            this.appendTo($('body'));
        }

        private addEvents():void {
            this.getEl().on('click', (event) => {

                // Make sure component is not deselected when the toolbar is clicked.
                event.stopPropagation();

                // Simple editor command implementation ;)
                var tag = event.target.getAttribute('live-edit-data-tag');
                if (tag) {
                    $(window).trigger('editorToolbarButtonClick.liveEdit', [tag]);
                }
            });

            $(window).scroll(() => {
                if (this.selectedParagraph) {
                    this.updatePosition();
                }
            });
        }

        private show(component:LiveEdit.component.Component):void {
            this.selectedParagraph = component;

            // For some reason. JQuery outerWidth returns a very incorrect number after show() is called.
            // Update positions before show.
            this.updatePosition();
            this.getEl().show(null);
            this.toggleArrowPosition(false);
        }

        private hide():void {
            this.selectedParagraph = null;
            this.getEl().hide(null);
        }

        private updatePosition():void {
            if (!this.selectedParagraph) {
                return;
            }

            var defaultPosition = this.getPositionRelativeToComponentTop();

            var stick = $(window).scrollTop() >= this.selectedParagraph.getElement().offset().top - 60;

            var el = this.getEl();

            if (stick) {
                el.css({
                    position: 'fixed',
                    top: 10,
                    left: defaultPosition.left
                });
            } else {
                el.css({
                    position: 'absolute',
                    top: defaultPosition.top,
                    left: defaultPosition.left
                });
            }

            var placeArrowOnTop = $(window).scrollTop() >= defaultPosition.bottom - 10;

            this.toggleArrowPosition(placeArrowOnTop);
        }

        private toggleArrowPosition(showArrowAtTop:Boolean):void {
            if (showArrowAtTop) {
                this.getEl().removeClass('live-edit-arrow-bottom').addClass('live-edit-arrow-top');
            } else {
                this.getEl().removeClass('live-edit-arrow-top').addClass('live-edit-arrow-bottom');
            }
        }

        private getPositionRelativeToComponentTop():any {
            var dimensions:ElementDimensions = this.selectedParagraph.getElementDimensions(),
                leftPos = dimensions.left + (dimensions.width / 2 - this.getEl().outerWidth() / 2),
                topPos = dimensions.top - this.getEl().height() - 25;

            return {
                left: leftPos,
                top: topPos,
                bottom: dimensions.top + dimensions.height
            };
        }
    }

}