module LiveEdit.ui {
    var $ = $liveedit;

    var componentHelper = LiveEdit.ComponentHelper;

    export class Cursor extends LiveEdit.ui.Base {

        constructor() {
            super();
            this.registerGlobalListeners();
        }


        private registerGlobalListeners() {
            $(window).on('component.mouseOver', $.proxy(this.updateCursor, this));
            $(window).on('component.mouseOut', $.proxy(this.resetCursor, this));
            $(window).on('component.onSelect', $.proxy(this.updateCursor, this));
        }


        private updateCursor(event, $component) {
            var componentType = LiveEdit.ComponentHelper.getComponentType($component);
            var $body = $('body');
            var cursor = 'default';

            switch (componentType) {
                case 'region':
                    cursor = 'pointer';
                    break;
                case 'part':
                    cursor = 'move';
                    break;
                case 'layout':
                    cursor = 'move';
                    break;
                case 'paragraph':
                    cursor = 'move';
                    break;
                default:
                    cursor = 'default';
            }

            $body.css('cursor', cursor);
        }


        private resetCursor() {
            $('body').css('cursor', 'default');
        }

    }
}