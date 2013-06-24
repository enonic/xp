module LiveEdit.ui {
    var $ = $liveEdit;

    export class Cursor extends LiveEdit.ui.Base {

        private DEFAULT_CURSOR:string = 'default';

        constructor() {
            super();
            this.registerGlobalListeners();
        }

        private registerGlobalListeners():void {
            $(window).on('mouseOver.liveEdit.component', (event:JQueryEventObject, component) => this.update(component));
            $(window).on('select.liveEdit.component', (event:JQueryEventObject, component:JQuery) => this.update(component));
            $(window).on('mouseOut.liveEdit.component', () => this.reset());
        }

        private update(component:JQuery):void {
            var componentType = LiveEdit.ComponentHelper.getComponentType(component);
            var body:JQuery = $('body');
            var cursor:string = this.DEFAULT_CURSOR;

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
            body.css('cursor', cursor);
        }

        private reset():void {
            if(LiveEdit.DragDropSort.isDragging()) {
                return;
            }
            $('body').css('cursor', this.DEFAULT_CURSOR);
        }

    }
}