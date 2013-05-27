module LiveEdit.ui {
    var $ = $liveedit;

    export class Cursor extends LiveEdit.ui.Base {
        constructor() {
            super();
            this.registerGlobalListeners();
        }

        private registerGlobalListeners():void {
            $(window).on('mouseOver.liveEdit.component', (event:JQueryEventObject, component) => this.updateCursor(component));
            $(window).on('select.liveEdit.component', (event:JQueryEventObject, component:JQuery) => this.updateCursor(component));
            $(window).on('mouseOut.liveEdit.component', () => this.resetCursor());
        }

        private updateCursor(component:JQuery):void {
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

        private resetCursor():void {
            $('body').css('cursor', 'default');
        }

    }
}