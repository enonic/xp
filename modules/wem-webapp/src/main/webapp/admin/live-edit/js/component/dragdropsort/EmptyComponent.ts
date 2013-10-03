module LiveEdit.component.dragdropsort {

    // Uses
    var $ = $liveEdit;

    export class EmptyComponent {

        public static createEmptyComponentHtml(component:LiveEdit.component.Component):string {

            return '<div class="live-edit-empty-component" data-live-edit-empty-component="true" data-live-edit-type="' + component.getComponentType().getName() + '">' +
                   '    <div class="' + component.getComponentType().getIconCls() + ' live-edit-empty-component-icon"></div>' +
                   '</div>';
        }

        public static loadComponent(componentKey:string):void {
            var selectedComponent = LiveEdit.component.Selection.getSelectedComponent();

            if (!selectedComponent.isEmpty()) {
                return;
            }

            var componentUrl:string = '../../admin2/live-edit/data/mock-component-' + componentKey + '.html';

            $.ajax({
                url: componentUrl,
                cache: false,
                beforeSend: () => {
                    LiveEdit.component.dragdropsort.EmptyComponent.appendLoadingSpinner(selectedComponent)
                },
                success: (responseHtml:string) => {
                    LiveEdit.component.dragdropsort.EmptyComponent.replaceEmptyComponent(selectedComponent, $(responseHtml))
                }
            });
        }

        private static replaceEmptyComponent(selectedComponent:LiveEdit.component.Component, responseHtml:JQuery):void {

            var emptyComponentEl:JQuery = selectedComponent.getElement();

            emptyComponentEl.replaceWith(responseHtml);

            LiveEdit.component.Selection.select(new LiveEdit.component.Component(responseHtml));

            $(window).trigger('sortableUpdate.liveEdit');

            // It seems like it is not possible to add new sortables (region in layout) to the existing sortable
            // So we have to create it again.
            // Ideally we should destroy the existing sortable first before creating.
            if (selectedComponent.getComponentType().getType() == LiveEdit.component.Type.LAYOUT) {
                LiveEdit.component.dragdropsort.DragDropSort.createJQueryUiSortable();
            }

        }

        private static appendLoadingSpinner(emptyComponent:LiveEdit.component.Component):void {
            var element:JQuery = emptyComponent.getElement();
            element.children('.live-edit-empty-component-icon').addClass('live-edit-font-icon-spinner');
        }

    }
}
