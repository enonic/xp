module LiveEdit.ui {
    var $ = $liveedit;

    var componentHelper = LiveEdit.ComponentHelper;

    export class EditorToolbar extends LiveEdit.ui.Base {

        private selectedComponent:JQuery = null;

        constructor() {
            super();

            this.selectedComponent = null;

            this.addView();
            this.addEvents();
            this.registerGlobalListeners();

            console.log('EditorToolbar instantiated. Using jQuery ' + $().jquery);
        }

        registerGlobalListeners():void {
            $(window).on('paragraphEdit.liveEdit.component', (event:JQueryEventObject, component:JQuery) => this.show(component));
            $(window).on('paragraphLeave.liveEdit.component', () => this.hide());
            $(window).on('remove.liveEdit.component', () => this.hide());
            $(window).on('sortStart.liveEdit.component', () => this.hide());
        }

        addView():void {
            var html = '<div class="live-edit-editor-toolbar live-edit-arrow-bottom" style="display: none">' +
                '    <button data-tag="paste" class="live-edit-editor-button"></button>' +
                '    <button data-tag="insertUnorderedList" class="live-edit-editor-button"></button>' +
                '    <button data-tag="insertOrderedList" class="live-edit-editor-button"></button>' +
                '    <button data-tag="link" class="live-edit-editor-button"></button>' +
                '    <button data-tag="cut" class="live-edit-editor-button"></button>' +
                '    <button data-tag="strikeThrough" class="live-edit-editor-button"></button>' +
                '    <button data-tag="bold" class="live-edit-editor-button"></button>' +
                '    <button data-tag="underline" class="live-edit-editor-button"></button>' +
                '    <button data-tag="italic" class="live-edit-editor-button"></button>' +
                '    <button data-tag="superscript" class="live-edit-editor-button"></button>' +
                '    <button data-tag="subscript" class="live-edit-editor-button"></button>' +
                '    <button data-tag="justifyLeft" class="live-edit-editor-button"></button>' +
                '    <button data-tag="justifyCenter" class="live-edit-editor-button"></button>' +
                '    <button data-tag="justifyRight" class="live-edit-editor-button"></button>' +
                '    <button data-tag="justifyFull" class="live-edit-editor-button"></button>' +
                '</div>';

            this.createElement(html);
            this.appendTo($('body'));
        }


        addEvents():void {
            this.getRootEl().on('click', (event) => {

                // Make sure component is not deselected when the toolbar is clicked.
                event.stopPropagation();

                // Simple editor command implementation ;)
                var tag = event.target.getAttribute('data-tag');
                if (tag) {
                    $(window).trigger('buttonClick.liveEdit.editorToolbar', [tag]);
                }
            });

            $(window).scroll(() => {
                if (this.selectedComponent) {
                    this.updatePosition();
                }
            });
        }

        show(component:JQuery):void {
            this.selectedComponent = component;

            this.getRootEl().show(null);
            this.toggleArrowPosition(false);
            this.updatePosition();
        }

        hide():void {
            this.selectedComponent = null;
            this.getRootEl().hide(null);
        }

        updatePosition():void {
            if (!this.selectedComponent) {
                return;
            }

            var defaultPosition = this.getPositionRelativeToComponentTop();

            var stick = $(window).scrollTop() >= this.selectedComponent.offset().top - 60;

            var el = this.getRootEl();

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


        toggleArrowPosition(showArrowAtTop:Boolean):void {
            if (showArrowAtTop) {
                this.getRootEl().removeClass('live-edit-arrow-bottom').addClass('live-edit-arrow-top');
            } else {
                this.getRootEl().removeClass('live-edit-arrow-top').addClass('live-edit-arrow-bottom');
            }
        }


        getPositionRelativeToComponentTop():any {
            var componentBox = componentHelper.getBoxModel(this.selectedComponent),
                leftPos = componentBox.left + (componentBox.width / 2 - this.getRootEl().outerWidth() / 2),
                topPos = componentBox.top - this.getRootEl().height() - 25;

            return {
                left: leftPos,
                top: topPos,
                bottom: componentBox.top + componentBox.height
            };
        }
    }

}