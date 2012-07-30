AdminLiveEdit.Highlighter = (function () {

    var REGION_BORDER_COLOR     = '#141414';
    var WINDOW_BORDER_COLOR     = '#141414';
    var CONTENT_BORDER_COLOR    = '#141414';
    var PARAGRAPH_BORDER_COLOR  = '#141414';

    var $selectedComponent = [];


    function createHighlighter() {
        $liveedit('body')
            .append($liveedit('<svg xmlns="http://www.w3.org/2000/svg" version="1.1" id="live-edit-highlighter" style="top:-5000px;left:-5000px"><rect width="150" height="150"/></svg>'));
    }


    function createBorderForSelectedComponent() {
        $liveedit('body')
            .append($liveedit('<svg xmlns="http://www.w3.org/2000/svg" version="1.1" id="live-edit-selected-border"><rect width="150" height="150"/></svg>'));
    }


    function getSelectedComponent() {
        return $selectedComponent;
    }


    function setSelectedComponent($component) {
        $selectedComponent = $component;
    }


    function hideSelectedBorder() {
        var $border = $liveedit('#live-edit-selected-border');
        $liveedit('body').append($border);

        $border.css({
            top : '-5000px',
            left: '-5000px'
        });
    }


    function hideHighlighter() {
        var $highlighter = $liveedit('#live-edit-highlighter');
        $highlighter.css({
            top : '-5000px',
            left: '-5000px'
        });
        $liveedit.publish('/highlighter/hide');
    }


    function deselectComponent() {
        $liveedit('.live-edit-selected-component').removeClass('live-edit-selected-component');
        hideSelectedBorder();
        hideHighlighter();

        setSelectedComponent($liveedit());

        $liveedit.publish('/page/component/deselect');
    }


    function addSelectedBorder($component) {
        var $border = $liveedit('#live-edit-selected-border');
        var $borderRect = $border.find('rect');
        var componentBoxModel = AdminLiveEdit.Util.getBoxModel($component);

        // Add position relative to the page component in order have absolute positioned elements inside.
        $liveedit('.live-edit-selected-component').removeClass('live-edit-selected-component');
        $component.addClass('live-edit-selected-component');
        var w = componentBoxModel.width;
        var h = componentBoxModel.height;
        /*
        $border.css({
            width   : componentBoxModel.width,
            height  : componentBoxModel.height,
            top     : 0,
            left    : 0
        });
        $borderRect[0].setAttribute('width', componentBoxModel.width);
        $borderRect[0].setAttribute('height', componentBoxModel.height);

        var componentType = AdminLiveEdit.Util.getPageComponentType($component);
        switch (componentType) {
        case 'region':
            $border.css('stroke', REGION_BORDER_COLOR);
            break;
        case 'window':
            $border.css('stroke', WINDOW_BORDER_COLOR);
            break;
        case 'content':
            $border.css('stroke', CONTENT_BORDER_COLOR);
            break;
        case 'paragraph':
            $border.css('stroke', PARAGRAPH_BORDER_COLOR);
            break;
        default:
            $border.css('stroke', 'red');
        }
        */
        // $component.append($border);
    }


    function selectComponent($component) {
        var $selected = getSelectedComponent();

        if ($selected.length > 0 && $component[0] === $selected[0]) {
            deselectComponent();
            return;
        }
        addSelectedBorder($component);

        hideHighlighter();

        setSelectedComponent($component);

        $liveedit.publish('/page/component/select', [$component]);
    }


    function selectParentComponent() {
        var $parent = getSelectedComponent().parents('[data-live-edit-type]');
        if ($parent && $parent.length > 0) {
            selectComponent($liveedit($parent[0]));
        }
    }


    function highlight($component) {
        var componentBoxModel = AdminLiveEdit.Util.getBoxModel($component);
        var w       = Math.round(componentBoxModel.width);
        var h       = Math.round(componentBoxModel.height);
        var top     = Math.round(componentBoxModel.top);
        var left    = Math.round(componentBoxModel.left);

        var $highlighter = $liveedit('#live-edit-highlighter');
        var $highlighterRect = $highlighter.find('rect');

        $highlighter.width(w);
        $highlighter.height(h);
        $highlighterRect[0].setAttribute('width', w);
        $highlighterRect[0].setAttribute('height', h);
        $highlighter.css({
            top : top,
            left: left
        });

        var componentType = AdminLiveEdit.Util.getPageComponentType($component);
        switch (componentType) {
        case 'region':
            $highlighter.css('stroke', REGION_BORDER_COLOR);
            break;
        case 'window':
            $highlighter.css('stroke', WINDOW_BORDER_COLOR);
            break;
        case 'content':
            $highlighter.css('stroke', CONTENT_BORDER_COLOR);
            break;
        case 'paragraph':
            $highlighter.css('stroke', PARAGRAPH_BORDER_COLOR);
            break;
        default:
            $highlighter.css('stroke', 'red');
        }

        $liveedit.publish('/page/component/highlight', [$component]);
    }




    function attachEventListeners() {
        $liveedit(document).on('mouseover', '[data-live-edit-type]', function (event) {
            var $component = $liveedit(this);

            var disableHover = $component.hasClass('live-edit-selected-component')
                                   || AdminLiveEdit.DragDrop.isDragging() || $component.find('.live-edit-selected-component').length > 0;
            if (disableHover) {
                return;
            }
            event.stopPropagation();
            highlight($component);

            // TODO: Use class and remove on mouse out.
            $component.css('cursor', 'pointer');
        });

        $liveedit('body').on('mouseover', function (event) {
            AdminLiveEdit.Highlighter.hide();
        });

        $liveedit('body').on('click touchstart', '[data-live-edit-type]', function (event) {
            event.stopPropagation();
            event.preventDefault();

            var $closestFromTarget = $liveedit(event.target).closest('[data-live-edit-type]');

            selectComponent($closestFromTarget);

            return false;
        });

        $liveedit(document).on('click', function (event) {
            deselectComponent();
        });
    }


    function registerSubscribers() {
        $liveedit.subscribe('/page/component/dragstart', hideHighlighter);
    }


    function init() {
        createHighlighter();
        createBorderForSelectedComponent();
        attachEventListeners();
    }

    // ***********************************************************************************************************************************//
    // Define public methods

    return {
        init: init,

        highlight: function ($component) {
            highlight($component);
        },

        hide: function () {
            hideHighlighter();
        },
        select: function ($component) {
            selectComponent($component);
        },

        selectParent: function () {
            selectParentComponent();
        },

        deselect: function () {
            deselectComponent();
        },

        getSelected: function () {
            return $selectedComponent;
        }
    };

}());