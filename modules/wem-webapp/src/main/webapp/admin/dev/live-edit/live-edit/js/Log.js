AdminLiveEdit.Log = (function () {

    function init() {
        var util = AdminLiveEdit.Util;

        function logSortEvent(event, uiEvent, ui) {
            console.groupCollapsed('Publish: ' + event.type);
            console.log('Type: ' + util.getPageComponentType(ui.item));
            console.log('Key: ' + util.getPageComponentKey(ui.item));
            console.log('Name: ' + util.getPageComponentName(ui.item));
            console.log('ui:');
            console.dir(ui);
            console.groupEnd();
        }

        $liveedit.subscribe('/page/component/highlight', function (event, $component) {
            console.groupCollapsed('Publish: ' + event.type);
            console.log('Type: ' + util.getPageComponentType($component));
            console.log('Key: ' + util.getPageComponentKey($component));
            console.log('Name: ' + util.getPageComponentName($component));
            console.dirxml($component[0]);
            console.groupEnd();
        });

        $liveedit.subscribe('/page/component/select', function (event, $component) {
            console.groupCollapsed('Publish: ' + event.type);
            console.log('Type: ' + util.getPageComponentType($component));
            console.log('Key: ' + util.getPageComponentKey($component));
            console.log('Name: ' + util.getPageComponentName($component));
            console.dirxml($component[0]);
            console.groupEnd();
        });

        $liveedit.subscribe('/page/component/deselect', function (event) {
            console.groupCollapsed('Publish: ' + event.type);
            console.groupEnd();
        });

        $liveedit.subscribe('/page/component/dragstart', function (event, uiEvent, ui) {
            logSortEvent(event, uiEvent, ui);
        });

        $liveedit.subscribe('/page/component/dragover', function (event, uiEvent, ui) {
            logSortEvent(event, uiEvent, ui);
        });

        $liveedit.subscribe('/page/component/sortchange', function (event, uiEvent, ui) {
            logSortEvent(event, uiEvent, ui);
        });

        $liveedit.subscribe('/page/component/sortupdate', function (event, uiEvent, ui) {
            logSortEvent(event, uiEvent, ui);
        });

        $liveedit.subscribe('/page/component/sortstop', function (event, uiEvent, ui) {
            logSortEvent(event, uiEvent, ui);
        });
    }

    // ********************************************************************************************************************************** //
    // Define public methods

    return {
        init: init
    };

}());