AdminLiveEdit.ui.Highlighter = (function () {

    var PAGE_BORDER_COLOR       = '#141414';
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
        // $liveedit.publish('/highlighter/hide');
    }


    function scrollComponentIntoView($component) {
        var util = AdminLiveEdit.Util;
        var componentTopPosition = util.getPageComponentPagePosition($component).top;
        if (componentTopPosition <= window.pageYOffset) {
            $liveedit('html, body').animate({scrollTop: componentTopPosition - 10}, 200);
        }
    }


    function deselectComponent() {
        $liveedit('.live-edit-selected-component').removeClass('live-edit-selected-component');
        hideSelectedBorder();
        hideHighlighter();

        setSelectedComponent($liveedit());
    }


    function addSelectedBorder($component) {
        var $border = $liveedit('#live-edit-selected-border');
        var $borderRect = $border.find('rect');
        var componentBoxModel = AdminLiveEdit.Util.getBoxModel($component);

        // Add position relative to the page component in order have absolute positioned elements inside.
        $liveedit('.live-edit-selected-component').removeClass('live-edit-selected-component');
        $component.addClass('live-edit-selected-component');

        /*TODO: Should we add the border or not*/
        /*
        var w = componentBoxModel.width;
        var h = componentBoxModel.height;
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


    function selectComponent(event, $component) {
        addSelectedBorder($component);
        setSelectedComponent($component);
        scrollComponentIntoView($component);
    }


    function selectParentComponent() {
        var $parent = getSelectedComponent().parents('[data-live-edit-type]');
        if ($parent && $parent.length > 0) {
            $liveedit.publish('/page/component/select', [$liveedit($parent[0])]);
        }
    }


    function highlight(event, $component) {
        var componentType = AdminLiveEdit.Util.getComponentType($component);
        var tagName = AdminLiveEdit.Util.getTagNameForComponent($component);
        var componentBoxModel = AdminLiveEdit.Util.getBoxModel($component);
        var w       = Math.round(componentBoxModel.width);
        var h       = Math.round(componentBoxModel.height);
        var top     = Math.round(componentBoxModel.top);
        var left    = Math.round(componentBoxModel.left);

        // We need to get the full height of the page/document.
        if (componentType === 'page' && tagName === 'body') {
            h = AdminLiveEdit.Util.getDocumentSize().height;
        }

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
        case 'page':
            $highlighter.css('stroke', PAGE_BORDER_COLOR);
            break;
        default:
            $highlighter.css('stroke', 'red');
        }
    }


    function registerSubscribers() {
        $liveedit.subscribe('/page/component/highlight', highlight);
        $liveedit.subscribe('/page/component/hide-highlighter', hideHighlighter);
        $liveedit.subscribe('/page/component/select', selectComponent);
        $liveedit.subscribe('/page/component/select-parent', selectParentComponent);
        $liveedit.subscribe('/page/component/deselect', deselectComponent);
        $liveedit.subscribe('/page/component/sortstart', hideHighlighter);
        $liveedit.subscribe('/page/component/sortstop', function (event, uiEvent, ui) {
            $liveedit.publish('/page/component/select', [ui.item]);
        });
    }


    function init() {
        createHighlighter();
        createBorderForSelectedComponent();
        registerSubscribers();
    }

    // ***********************************************************************************************************************************//
    // Define public methods

    return {
        init: init,

        getSelected: function () {
            return $selectedComponent;
        }
    };

}());