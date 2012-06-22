AdminLiveEdit.ElementSelector = (function () {
    var selected = null;

    function setSelected(element) {
        selected = element;
    }


    function getSelected() {
        return selected;
    }


    function deselect() {
        AdminLiveEdit.PageOverlay.hide();
        AdminLiveEdit.Tooltip.hide();
        AdminLiveEdit.Toolbar.hide();

        setSelected(null);
        var dragDrop = AdminLiveEdit.DragDrop;
        if (!dragDrop.isDragging()) {
            dragDrop.disable();
        }
    }


    function selectElement(element) {
        if (getSelected() && element[0] === getSelected()[0]) {
            deselect();
            return;
        }

        var pageOverlay = AdminLiveEdit.PageOverlay;
        var util = AdminLiveEdit.Util;
        var dragDrop = AdminLiveEdit.DragDrop;
        var highlighter = AdminLiveEdit.Highlighter;
        var tooltip = AdminLiveEdit.Tooltip;
        var toolbar = AdminLiveEdit.Toolbar;
        var pageElementTypeToSelect = util.getPageElementType(element);
        var elementBoxModel;

        if (pageElementTypeToSelect === 'window') {
            elementBoxModel = util.getBoxModel(element);
            highlighter.highlightWindow(element, true);
        } else if (pageElementTypeToSelect === 'region') {
            elementBoxModel = util.getBoxModel(element, true);
            highlighter.highlightRegion(element, true);
        }

        pageOverlay.clearRectangle(elementBoxModel.left, elementBoxModel.top, elementBoxModel.width,
            elementBoxModel.height);

        setSelected(element);
        tooltip.moveToPageElement(element);
        toolbar.moveTo(element);

        dragDrop.enable();
    }


    function initMouseClickEvent() {
        $liveedit('body').on('click', '[data-live-edit-type=region], [data-live-edit-type=window]', function (event) {
            event.stopPropagation();
            selectElement($liveedit(this));
        });
    }


    function init() {
        initMouseClickEvent();
    }


    // *****************************************************************************************************************
    // Public

    return {
        init: function () {
            init();
        },

        select: function (element) {
            selectElement(element);
        },

        deselect: function () {
            deselect();
        },

        setSelected: function (element) {
            setSelected(element);
        },

        getSelected: function () {
            return selected;
        }
    };

}());