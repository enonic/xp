module LiveEdit.component {

    // Uses
    var $ = $liveEdit;

    export class ComponentInserter {

        public static insert(componentKey:string):void {
            var selectedComponent = LiveEdit.Selection.getSelectedComponent();

            if (selectedComponent.isEmpty()) {
                var componentUrl:string = '/admin2/live-edit/data/mock-component-' + componentKey + '.html';

                $.ajax({
                    url: componentUrl,
                    cache: false,
                    beforeSend: () => {
                        LiveEdit.component.ComponentInserter.appendLoadingSpinner(selectedComponent)
                    },
                    success: (responseHtml:string) => {
                        LiveEdit.component.ComponentInserter.replaceEmptyComponent(selectedComponent, $(responseHtml))
                    }
                });
            }
        }

        private static replaceEmptyComponent(selectedComponent:LiveEdit.component.Component, responseHtml:JQuery):void {

            var emptyComponentEl:JQuery = selectedComponent.getElement();

            emptyComponentEl.replaceWith(responseHtml);

            LiveEdit.Selection.setSelectionOnElement(responseHtml);

            $(window).trigger('selectComponent.liveEdit', [new LiveEdit.component.Component(responseHtml)]);

            $(window).trigger('sortableUpdate.liveEdit');

            // It seems like it is not possible to add new sortables (region in layout) to the existing sortable
            // So we have to create it again.
            // Ideally we should destroy the existing sortable first before creating.
            if (selectedComponent.getComponentType().getType() == LiveEdit.component.Type.LAYOUT) {
                LiveEdit.component.DragDropSort.createJQueryUiSortable();
            }

        }

        private static appendLoadingSpinner(emptyComponent:LiveEdit.component.Component):void {
            emptyComponent.getElement().append('<img src="/admin2/live-edit/images/spinner.png"/>');
        }

    }
}
