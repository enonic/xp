// fixme: pass Live Edit Component reference instead of a JQuery object in triggers
module LiveEdit.component.observer {
    var $ = $liveEdit;

    export class Base {
        public cssSelector:string = '';

        constructor() {
        }

        attachMouseOverEvent():void {

            $(document).on('mouseover', this.cssSelector, (event:JQueryEventObject) => {
                if (this.cancelMouseOverEvent(event)) {
                    return;
                }
                $('.live-edit-selected-component').removeClass('live-edit-selected-component');

                event.stopPropagation();

                $(window).trigger('mouseOverComponent.liveEdit', [$(event.currentTarget)]);
            });
        }

        attachMouseOutEvent():void {

            $(document).on('mouseout', () => {
                if (this.hasComponentSelected()) {
                    return;
                }
                $(window).trigger('mouseOutComponent.liveEdit');
            });
        }

        attachClickEvent():void {

            $(document).on('click contextmenu touchstart', this.cssSelector, (event:JQueryEventObject) => {
                // Is this needed? We are using $.on with a delegate so the target would always be a LE component
                if (this.isLiveEditUiComponent($(event.target))) {
                    return;
                }
                event.stopPropagation();
                event.preventDefault();

                var component:JQuery = $(event.currentTarget),
                    componentIsSelected = component.hasClass('live-edit-selected-component'),
                    pageHasComponentSelected = $('.live-edit-selected-component').length > 0;

                if (componentIsSelected || pageHasComponentSelected) {
                    $(window).trigger('deselectComponent.liveEdit');
                } else {

                    // Used by eg. Menu
                    var pagePosition:any = {
                        x: event.pageX,
                        y: event.pageY
                    };

                    $(window).trigger('selectComponent.liveEdit', [component, pagePosition]);
                }
            });
        }

        getAll():JQuery {
            return $(this.cssSelector);
        }

        cancelMouseOverEvent(event:JQueryEventObject):Boolean {
            var elementIsUiComponent = this.isLiveEditUiComponent($(event.target));
            return elementIsUiComponent || this.hasComponentSelected() || LiveEdit.DragDropSort.isDragging();
        }

        hasComponentSelected():Boolean {
            return $('.live-edit-selected-component').length > 0;
        }

        isLiveEditUiComponent(target:JQuery):Boolean {
            return target.is('[id*=live-edit-ui-cmp]') || target.parents('[id*=live-edit-ui-cmp]').length > 0;
        }

    }
}
