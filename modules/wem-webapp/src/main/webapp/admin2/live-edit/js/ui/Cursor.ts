module LiveEdit.ui {

    // Uses
    var $ = $liveEdit;

    export class Cursor extends LiveEdit.ui.Base {

        constructor() {
            super();
            this.registerGlobalListeners();
        }

        private registerGlobalListeners():void {
            $(window).on('mouseOverComponent.liveEdit', (event:JQueryEventObject, component) => this.update(component));
            $(window).on('selectComponent.liveEdit', (event:JQueryEventObject, component) => this.update(component));
            $(window).on('mouseOutComponent.liveEdit', () => this.reset());
        }

        private update(component:LiveEdit.component.Component):void {
            var body:JQuery = $('body');

            body.css('cursor', component.getComponentType().getCursor());
        }

        private reset():void {
            if(LiveEdit.DragDropSort.isDragging()) {
                return;
            }
            $('body').css('cursor', 'default');
        }

    }
}