module LiveEdit.ui {
    var $ = $liveedit;

    export class Cursor extends LiveEdit.ui.Base {
        constructor() {
            super();
            this.registerGlobalListeners();
        }

        private registerGlobalListeners() {
            $(window).on('component.mouseOver component.onSelect', (event:JQueryEventObject, component:JQuery) => {
                this.updateCursor(event, component);
            });
            $(window).on('component.mouseOut', () => {
                this.resetCursor();
            });
        }

        private updateCursor(event, component:JQuery) {
            var componentType = LiveEdit.ComponentHelper.getComponentType(component);
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