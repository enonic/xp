var LiveEdit;
(function (LiveEdit) {
    var DomHelper = (function () {
        function DomHelper() { }
        DomHelper.$ = $liveedit;
        DomHelper.getDocumentSize = function getDocumentSize() {
            var $document = DomHelper.$(document);
            return {
                width: $document.width(),
                height: $document.height()
            };
        };
        DomHelper.getViewPortSize = function getViewPortSize() {
            var $window = DomHelper.$(window);
            return {
                width: $window.width(),
                height: $window.height()
            };
        };
        DomHelper.getDocumentScrollTop = function getDocumentScrollTop() {
            return DomHelper.$(document).scrollTop();
        };
        return DomHelper;
    })();
    LiveEdit.DomHelper = DomHelper;    
})(LiveEdit || (LiveEdit = {}));
var LiveEdit;
(function (LiveEdit) {
    var ComponentHelper = (function () {
        function ComponentHelper() { }
        ComponentHelper.$ = $liveedit;
        ComponentHelper.getBoxModel = function getBoxModel(component) {
            var cmp = component;
            var offset = cmp.offset();
            var top = offset.top;
            var left = offset.left;
            var width = cmp.outerWidth();
            var height = cmp.outerHeight();
            var bt = parseInt(cmp.css('borderTopWidth'), 10);
            var br = parseInt(cmp.css('borderRightWidth'), 10);
            var bb = parseInt(cmp.css('borderBottomWidth'), 10);
            var bl = parseInt(cmp.css('borderLeftWidth'), 10);
            var pt = parseInt(cmp.css('paddingTop'), 10);
            var pr = parseInt(cmp.css('paddingRight'), 10);
            var pb = parseInt(cmp.css('paddingBottom'), 10);
            var pl = parseInt(cmp.css('paddingLeft'), 10);
            return {
                top: top,
                left: left,
                width: width,
                height: height,
                borderTop: bt,
                borderRight: br,
                borderBottom: bb,
                borderLeft: bl,
                paddingTop: pt,
                paddingRight: pr,
                paddingBottom: pb,
                paddingLeft: pl
            };
        };
        ComponentHelper.getPagePositionForComponent = function getPagePositionForComponent(component) {
            var pos = component.position();
            return {
                top: pos.top,
                left: pos.left
            };
        };
        ComponentHelper.getComponentInfo = function getComponentInfo(component) {
            return {
                type: ComponentHelper.getComponentType(component),
                key: ComponentHelper.getComponentKey(component),
                name: ComponentHelper.getComponentName(component),
                tagName: ComponentHelper.getTagNameForComponent(component)
            };
        };
        ComponentHelper.getComponentType = function getComponentType(component) {
            return component.data('live-edit-type');
        };
        ComponentHelper.getComponentKey = function getComponentKey(component) {
            return component.data('live-edit-key');
        };
        ComponentHelper.getComponentName = function getComponentName(component) {
            return component.data('live-edit-name') || '[No Name]';
        };
        ComponentHelper.getTagNameForComponent = function getTagNameForComponent(component) {
            return component[0].tagName.toLowerCase();
        };
        ComponentHelper.supportsTouch = function supportsTouch() {
            return document.hasOwnProperty('ontouchend');
        };
        return ComponentHelper;
    })();
    LiveEdit.ComponentHelper = ComponentHelper;    
})(LiveEdit || (LiveEdit = {}));
var LiveEdit;
(function (LiveEdit) {
    var $ = $liveedit;
    var MutationObserver = (function () {
        function MutationObserver() {
            this.mutationSummary = null;
            this.observedComponent = null;
            this.registerGlobalListeners();
        }
        MutationObserver.prototype.registerGlobalListeners = function () {
            var me = this;
            $(window).on('component.onParagraphEdit', $.proxy(me.observe, me));
            $(window).on('shader.onClick', $.proxy(me.disconnect, me));
        };
        MutationObserver.prototype.observe = function (event, $component) {
            var me = this;
            var isAlreadyObserved = me.observedComponent && me.observedComponent[0] === $component[0];
            if(isAlreadyObserved) {
                return;
            }
            me.disconnect(event);
            me.observedComponent = $component;
            me.mutationSummary = new LiveEditMutationSummary({
                callback: function (summaries) {
                    me.onMutate(summaries, event);
                },
                rootNode: $component[0],
                queries: [
                    {
                        all: true
                    }
                ]
            });
            console.log('MutationObserver: start observing component', $component);
        };
        MutationObserver.prototype.onMutate = function (summaries, event) {
            if(summaries && summaries[0]) {
                var $targetComponent = $(summaries[0].target), targetComponentIsSelected = $targetComponent.hasClass('live-edit-selected-component'), componentIsNotSelectedAndMouseIsOver = !targetComponentIsSelected && event.type === 'component.mouseOver', componentIsParagraphAndBeingEdited = $targetComponent.attr('contenteditable');
                if(componentIsParagraphAndBeingEdited) {
                    $(window).trigger('component.onParagraphEdit', [
                        $targetComponent
                    ]);
                } else if(componentIsNotSelectedAndMouseIsOver) {
                    $(window).trigger('component.mouseOver', [
                        $targetComponent
                    ]);
                } else {
                    $(window).trigger('component.onSelect', [
                        $targetComponent
                    ]);
                }
            }
        };
        MutationObserver.prototype.disconnect = function (event) {
            var targetComponentIsSelected = (this.observedComponent && this.observedComponent.hasClass('live-edit-selected-component'));
            var componentIsSelectedAndUserMouseOut = event.type === 'component.mouseOut' && targetComponentIsSelected;
            if(componentIsSelectedAndUserMouseOut) {
                return;
            }
            this.observedComponent = null;
            if(this.mutationSummary) {
                this.mutationSummary.disconnect();
                this.mutationSummary = null;
                console.log('MutationObserver: disconnect');
            }
        };
        return MutationObserver;
    })();
    LiveEdit.MutationObserver = MutationObserver;    
})(LiveEdit || (LiveEdit = {}));
AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.DragDropSort');
AdminLiveEdit.DragDropSort = ((function ($) {
    'use strict';
    var componentHelper = LiveEdit.ComponentHelper;
    var isDragging = false;
    var cursorAt = LiveEdit.ComponentHelper.supportsTouch() ? {
        left: 15,
        top: 70
    } : {
        left: -10,
        top: -15
    };
    var regionSelector = '[data-live-edit-type=region]';
    var layoutSelector = '[data-live-edit-type=layout]';
    var partSelector = '[data-live-edit-type=part]';
    var paragraphSelector = '[data-live-edit-type=paragraph]';
    var itemsToSortSelector = layoutSelector + ',' + partSelector + ',' + paragraphSelector;
    function enableDragDrop() {
        $(regionSelector).sortable('enable');
    }
    function disableDragDrop() {
        $(regionSelector).sortable('disable');
    }
    function getDragHelperHtml(text) {
        return '<div id="live-edit-drag-helper" style="width: 150px; height: 16px;">' + '    <img id="live-edit-drag-helper-status-icon" src="../../../admin2/live-edit/images/drop-no.gif"/>' + '    <span id="live-edit-drag-helper-text" style="width: 134px;">' + text + '</span>' + '</div>';
    }
    function setDragHelperText(text) {
        $('#live-edit-drag-helper-text').text(text);
    }
    function createComponentBarDraggables() {
        var $componentBarComponents = $('.live-edit-component');
        var draggableOptions = {
            connectToSortable: regionSelector,
            addClasses: false,
            cursor: 'move',
            appendTo: 'body',
            zIndex: 5100000,
            revert: function (validDrop) {
            },
            cursorAt: cursorAt,
            helper: function () {
                return getDragHelperHtml('');
            },
            start: function (event, ui) {
                $(window).trigger('component.onDragStart', [
                    event, 
                    ui
                ]);
                setDragHelperText($(event.target).data('live-edit-component-name'));
                isDragging = true;
            },
            stop: function (event, ui) {
                $(window).trigger('component.onDragStop', [
                    event, 
                    ui
                ]);
                isDragging = false;
            }
        };
        $componentBarComponents.draggable(draggableOptions);
    }
    function createDragHelper(event, helper) {
        return $(getDragHelperHtml(componentHelper.getComponentName(helper)));
    }
    function refreshSortable() {
        $(regionSelector).sortable('refresh');
    }
    function updateHelperStatusIcon(status) {
        $('#live-edit-drag-helper-status-icon').attr('src', '../../../admin2/live-edit/images/drop-' + status + '.gif');
    }
    function targetIsPlaceholder($target) {
        return $target.hasClass('live-edit-drop-target-placeholder');
    }
    function handleSortStart(event, ui) {
        isDragging = true;
        var componentIsSelected = ui.item.hasClass('live-edit-selected-component');
        ui.item.data('live-edit-selected-on-sort-start', componentIsSelected);
        var targetComponentName = LiveEdit.ComponentHelper.getComponentName($(event.target));
        ui.placeholder.html('Drop component here' + '<div style="font-size: 10px;">' + targetComponentName + '</div>');
        refreshSortable();
        $(window).trigger('component.onSortStart', [
            event, 
            ui
        ]);
    }
    function handleDragOver(event, ui) {
        event.stopPropagation();
        var draggedItemIsLayoutComponent = ui.item.data('live-edit-component-type') === 'layout' || ui.item.data('live-edit-type') === 'layout', isDraggingOverLayoutComponent = ui.placeholder.closest(layoutSelector).length > 0;
        if(draggedItemIsLayoutComponent && isDraggingOverLayoutComponent) {
            updateHelperStatusIcon('no');
            ui.placeholder.hide();
        } else {
            updateHelperStatusIcon('yes');
            $(window).trigger('component.onSortOver', [
                event, 
                ui
            ]);
        }
    }
    function handleDragOut(event, ui) {
        if(targetIsPlaceholder($(event.srcElement))) {
            removePaddingFromLayoutComponent();
        }
        updateHelperStatusIcon('no');
        $(window).trigger('component.onSortOut', [
            event, 
            ui
        ]);
    }
    function handleSortChange(event, ui) {
        addPaddingToLayoutComponent($(event.target));
        updateHelperStatusIcon('yes');
        ui.placeholder.show();
        $(window).trigger('component.onSortChange', [
            event, 
            ui
        ]);
    }
    function handleSortUpdate(event, ui) {
        $(window).trigger('component.onSortUpdate', [
            event, 
            ui
        ]);
    }
    function handleSortStop(event, ui) {
        isDragging = false;
        removePaddingFromLayoutComponent();
        var draggedItemIsLayoutComponent = ui.item.data('live-edit-component-type') === 'layout' || ui.item.data('live-edit-type') === 'layout', targetIsInLayoutComponent = $(event.target).closest(layoutSelector).length > 0;
        if(draggedItemIsLayoutComponent && targetIsInLayoutComponent) {
            ui.item.remove();
        }
        if(LiveEdit.ComponentHelper.supportsTouch()) {
            $(window).trigger('component.mouseOut');
        }
        var wasSelectedOnDragStart = ui.item.data('live-edit-selected-on-drag-start');
        $(window).trigger('component.onSortStop', [
            event, 
            ui, 
            wasSelectedOnDragStart
        ]);
        ui.item.removeData('live-edit-selected-on-drag-start');
    }
    function itemIsDraggedFromComponentBar(item) {
        return item.hasClass('live-edit-component');
    }
    function handleReceive(event, ui) {
        if(itemIsDraggedFromComponentBar(ui.item)) {
            var $componentBarComponent = $(this).children('.live-edit-component');
            var componentKey = $componentBarComponent.data('live-edit-component-key');
            var componentType = $componentBarComponent.data('live-edit-component-type');
            var url = '../../../admin2/live-edit/data/mock-component-' + componentKey + '.html';
            $componentBarComponent.hide();
            $.ajax({
                url: url,
                cache: false
            }).done(function (html) {
                $componentBarComponent.replaceWith(html);
                if(componentType === 'layout') {
                    createSortable();
                }
                $(window).trigger('component.onSortUpdate');
            });
        }
    }
    function addPaddingToLayoutComponent($component) {
        $component.closest(layoutSelector).addClass('live-edit-component-padding');
    }
    function removePaddingFromLayoutComponent() {
        $('.live-edit-component-padding').removeClass('live-edit-component-padding');
    }
    function registerGlobalListeners() {
        $(window).on('componentBar.dataLoaded', function () {
            createComponentBarDraggables();
        });
        $(window).on('component.onSelect', function (event, $component) {
        });
        $(window).on('component.onDeselect', function () {
            if(LiveEdit.ComponentHelper.supportsTouch() && !isDragging) {
                disableDragDrop();
            }
        });
        $(window).on('component.onParagraphSelect', function () {
            $(regionSelector).sortable('option', 'cancel', '[data-live-edit-type=paragraph]');
        });
        $(window).on('component.onParagraphEditLeave', function () {
            $(regionSelector).sortable('option', 'cancel', '');
        });
    }
    function createSortable() {
        $(regionSelector).sortable({
            revert: false,
            connectWith: regionSelector,
            items: itemsToSortSelector,
            distance: 1,
            delay: 150,
            tolerance: 'pointer',
            cursor: 'move',
            cursorAt: cursorAt,
            scrollSensitivity: Math.round(LiveEdit.DomHelper.getViewPortSize().height / 8),
            placeholder: 'live-edit-drop-target-placeholder',
            helper: createDragHelper,
            zIndex: 1001000,
            start: handleSortStart,
            over: handleDragOver,
            out: handleDragOut,
            change: handleSortChange,
            receive: handleReceive,
            update: handleSortUpdate,
            stop: handleSortStop
        });
    }
    function init() {
        createSortable();
        registerGlobalListeners();
    }
    return {
        initialize: init,
        enable: enableDragDrop,
        disable: disableDragDrop,
        isDragging: function () {
            return isDragging;
        }
    };
})($liveedit));
var LiveEdit;
(function (LiveEdit) {
    (function (model) {
        var $ = $liveedit;
        var Base = (function () {
            function Base() {
                this.cssSelector = '';
            }
            Base.prototype.attachMouseOverEvent = function () {
                var me = this;
                $(document).on('mouseover', me.cssSelector, function (event) {
                    var $component = $(this);
                    var targetIsUiComponent = me.isLiveEditUiComponent($(event.target));
                    var cancelEvents = targetIsUiComponent || me.hasComponentSelected() || AdminLiveEdit.DragDropSort.isDragging();
                    if(cancelEvents) {
                        return;
                    }
                    event.stopPropagation();
                    $(window).trigger('component.mouseOver', [
                        $component
                    ]);
                });
            };
            Base.prototype.attachMouseOutEvent = function () {
                var me = this;
                $(document).on('mouseout', function () {
                    if(me.hasComponentSelected()) {
                        return;
                    }
                    $(window).trigger('component.mouseOut');
                });
            };
            Base.prototype.attachClickEvent = function () {
                var me = this;
                $(document).on('click contextmenu touchstart', me.cssSelector, function (event) {
                    if(me.isLiveEditUiComponent($(event.target))) {
                        return;
                    }
                    event.stopPropagation();
                    event.preventDefault();
                    var $component = $(event.currentTarget), componentIsSelected = $component.hasClass('live-edit-selected-component'), pageHasComponentSelected = $('.live-edit-selected-component').length > 0;
                    if(componentIsSelected || pageHasComponentSelected) {
                        $(window).trigger('component.onDeselect');
                    } else {
                        var pagePosition = {
                            x: event.pageX,
                            y: event.pageY
                        };
                        $(window).trigger('component.onSelect', [
                            $component, 
                            pagePosition
                        ]);
                    }
                });
            };
            Base.prototype.hasComponentSelected = function () {
                return $('.live-edit-selected-component').length > 0;
            };
            Base.prototype.isLiveEditUiComponent = function ($target) {
                return $target.is('[id*=live-edit-ui-cmp]') || $target.parents('[id*=live-edit-ui-cmp]').length > 0;
            };
            Base.prototype.getAll = function () {
                return $(this.cssSelector);
            };
            return Base;
        })();
        model.Base = Base;        
    })(LiveEdit.model || (LiveEdit.model = {}));
    var model = LiveEdit.model;
})(LiveEdit || (LiveEdit = {}));
var __extends = this.__extends || function (d, b) {
    function __() { this.constructor = d; }
    __.prototype = b.prototype;
    d.prototype = new __();
};
var LiveEdit;
(function (LiveEdit) {
    (function (model) {
        var $ = $liveedit;
        var Page = (function (_super) {
            __extends(Page, _super);
            function Page() {
                        _super.call(this);
                this.cssSelector = '[data-live-edit-type=page]';
                this.attachClickEvent();
                console.log('Page model instantiated. Using jQuery ' + $().jquery);
            }
            return Page;
        })(LiveEdit.model.Base);
        model.Page = Page;        
    })(LiveEdit.model || (LiveEdit.model = {}));
    var model = LiveEdit.model;
})(LiveEdit || (LiveEdit = {}));
var LiveEdit;
(function (LiveEdit) {
    (function (model) {
        var $ = $liveedit;
        var componentHelper = LiveEdit.ComponentHelper;
        var Region = (function (_super) {
            __extends(Region, _super);
            function Region() {
                        _super.call(this);
                this.cssSelector = '[data-live-edit-type=region]';
                this.renderEmptyPlaceholders();
                this.attachMouseOverEvent();
                this.attachMouseOutEvent();
                this.attachClickEvent();
                this.registerGlobalListeners();
                console.log('Region model instantiated. Using jQuery ' + $().jquery);
            }
            Region.prototype.registerGlobalListeners = function () {
                $(window).on('component.onSortUpdate', $.proxy(this.renderEmptyPlaceholders, this));
                $(window).on('component.onSortOver', $.proxy(this.renderEmptyPlaceholders, this));
                $(window).on('component.onRemove', $.proxy(this.renderEmptyPlaceholders, this));
            };
            Region.prototype.renderEmptyPlaceholders = function () {
                var me = this;
                me.removeAllRegionPlaceholders();
                var $regions = me.getAll();
                $regions.each(function (index) {
                    var $region = $(this);
                    var regionIsEmpty = me.isRegionEmpty.call(me, $region);
                    if(regionIsEmpty) {
                        me.appendEmptyPlaceholder.call(me, $region);
                    }
                });
            };
            Region.prototype.appendEmptyPlaceholder = function ($region) {
                var html = '<div>Drag components here</div>';
                html += '<div style="font-size: 10px;">' + componentHelper.getComponentName($region) + '</div>';
                var $placeholder = $('<div/>', {
                    'class': 'live-edit-empty-region-placeholder',
                    'html': html
                });
                $region.append($placeholder);
            };
            Region.prototype.isRegionEmpty = function ($region) {
                var hasNotParts = $region.children('[data-live-edit-type]' + ':not(:hidden)').length === 0;
                var hasNotDropTargetPlaceholder = $region.children('.live-edit-drop-target-placeholder').length === 0;
                return hasNotParts && hasNotDropTargetPlaceholder;
            };
            Region.prototype.removeAllRegionPlaceholders = function () {
                $('.live-edit-empty-region-placeholder').remove();
            };
            return Region;
        })(LiveEdit.model.Base);
        model.Region = Region;        
    })(LiveEdit.model || (LiveEdit.model = {}));
    var model = LiveEdit.model;
})(LiveEdit || (LiveEdit = {}));
var LiveEdit;
(function (LiveEdit) {
    (function (model) {
        var $ = $liveedit;
        var Layout = (function (_super) {
            __extends(Layout, _super);
            function Layout() {
                        _super.call(this);
                this.cssSelector = '[data-live-edit-type=layout]';
                this.attachMouseOverEvent();
                this.attachMouseOutEvent();
                this.attachClickEvent();
                console.log('Layout model instantiated. Using jQuery ' + $().jquery);
            }
            return Layout;
        })(LiveEdit.model.Base);
        model.Layout = Layout;        
    })(LiveEdit.model || (LiveEdit.model = {}));
    var model = LiveEdit.model;
})(LiveEdit || (LiveEdit = {}));
var LiveEdit;
(function (LiveEdit) {
    (function (model) {
        var $ = $liveedit;
        var Part = (function (_super) {
            __extends(Part, _super);
            function Part() {
                        _super.call(this);
                this.cssSelector = '[data-live-edit-type=part]';
                this.renderEmptyPlaceholders();
                this.attachMouseOverEvent();
                this.attachMouseOutEvent();
                this.attachClickEvent();
                console.log('Part model instantiated. Using jQuery ' + $().jquery);
            }
            Part.prototype.appendEmptyPlaceholder = function ($part) {
                var $placeholder = $('<div/>', {
                    'class': 'live-edit-empty-part-placeholder',
                    'html': 'Empty Part'
                });
                $part.append($placeholder);
            };
            Part.prototype.isPartEmpty = function ($part) {
                return $($part).children().length === 0;
            };
            Part.prototype.renderEmptyPlaceholders = function () {
                var t = this;
                this.getAll().each(function (index) {
                    var $part = $(this);
                    var partIsEmpty = t.isPartEmpty($part);
                    if(partIsEmpty) {
                        t.appendEmptyPlaceholder($part);
                    }
                });
            };
            return Part;
        })(LiveEdit.model.Base);
        model.Part = Part;        
    })(LiveEdit.model || (LiveEdit.model = {}));
    var model = LiveEdit.model;
})(LiveEdit || (LiveEdit = {}));
var LiveEdit;
(function (LiveEdit) {
    (function (model) {
        var $ = $liveedit;
        var Content = (function (_super) {
            __extends(Content, _super);
            function Content() {
                        _super.call(this);
                this.cssSelector = '[data-live-edit-type=content]';
                this.attachMouseOverEvent();
                this.attachMouseOutEvent();
                this.attachClickEvent();
                console.log('Content model instantiated. Using jQuery ' + $().jquery);
            }
            return Content;
        })(LiveEdit.model.Base);
        model.Content = Content;        
    })(LiveEdit.model || (LiveEdit.model = {}));
    var model = LiveEdit.model;
})(LiveEdit || (LiveEdit = {}));
var LiveEdit;
(function (LiveEdit) {
    (function (model) {
        var $ = $liveedit;
        var Paragraph = (function (_super) {
            __extends(Paragraph, _super);
            function Paragraph() {
                        _super.call(this);
                this.selectedParagraph = null;
                this.modes = {
                };
                var me = this;
                this.cssSelector = '[data-live-edit-type=paragraph]';
                this.modes = {
                    UNSELECTED: 0,
                    SELECTED: 1,
                    EDIT: 2
                };
                this.currentMode = me.modes.UNSELECTED;
                this.attachMouseOverEvent();
                this.attachMouseOutEvent();
                this.attachClickEvent();
                this.registerGlobalListeners();
                console.log('Paragraph model instantiated. Using jQuery ' + $().jquery);
            }
            Paragraph.prototype.registerGlobalListeners = function () {
                $(window).on('shader.onClick', $.proxy(this.leaveEditMode, this));
                $(window).on('component.onDeselect', $.proxy(this.leaveEditMode, this));
            };
            Paragraph.prototype.attachClickEvent = function () {
                var me = this;
                $(document).on('click contextmenu touchstart', me.cssSelector, function (event) {
                    me.handleClick(event);
                });
            };
            Paragraph.prototype.handleClick = function (event) {
                var me = this;
                event.stopPropagation();
                event.preventDefault();
                if(me.selectedParagraph && !(me.currentMode === me.modes.EDIT)) {
                    me.selectedParagraph.css('cursor', '');
                }
                var $paragraph = $(event.currentTarget);
                if(!$paragraph.is(me.selectedParagraph)) {
                    me.currentMode = me.modes.UNSELECTED;
                }
                me.selectedParagraph = $paragraph;
                if(me.currentMode === me.modes.UNSELECTED) {
                    me.setSelectMode(event);
                } else if(me.currentMode === me.modes.SELECTED) {
                    me.setEditMode(event);
                } else {
                }
            };
            Paragraph.prototype.setSelectMode = function (event) {
                var me = this;
                me.selectedParagraph.css('cursor', 'url(../../../admin2/live-edit/images/pencil.png) 0 40, text');
                me.currentMode = me.modes.SELECTED;
                if(window.getSelection) {
                    window.getSelection().removeAllRanges();
                }
                var pagePosition = {
                    x: event.pageX,
                    y: event.pageY
                };
                $(window).trigger('component.onSelect', [
                    me.selectedParagraph, 
                    pagePosition
                ]);
                $(window).trigger('component.onParagraphSelect', [
                    me.selectedParagraph
                ]);
            };
            Paragraph.prototype.setEditMode = function (event) {
                var me = this, $paragraph = me.selectedParagraph;
                $(window).trigger('component.onParagraphEdit', [
                    me.selectedParagraph
                ]);
                $paragraph.css('cursor', 'text');
                $paragraph.addClass('live-edit-edited-paragraph');
                me.currentMode = me.modes.EDIT;
            };
            Paragraph.prototype.leaveEditMode = function (event) {
                var me = this, $paragraph = me.selectedParagraph;
                if($paragraph === null) {
                    return;
                }
                $(window).trigger('component.onParagraphEditLeave', [
                    me.selectedParagraph
                ]);
                $paragraph.css('cursor', '');
                $paragraph.removeClass('live-edit-edited-paragraph');
                me.selectedParagraph = null;
                me.currentMode = me.modes.UNSELECTED;
            };
            return Paragraph;
        })(LiveEdit.model.Base);
        model.Paragraph = Paragraph;        
    })(LiveEdit.model || (LiveEdit.model = {}));
    var model = LiveEdit.model;
})(LiveEdit || (LiveEdit = {}));
var LiveEdit;
(function (LiveEdit) {
    (function (ui) {
        var $ = $liveedit;
        var Base = (function () {
            function Base() {
            }
            Base.constructedCount = 0;
            Base.prototype.createElement = function (htmlString) {
                var id = Base.constructedCount++;
                this.element = $(htmlString);
                this.element.attr('id', 'live-edit-ui-cmp-' + id.toString());
                return this.element;
            };
            Base.prototype.appendTo = function (parent) {
                if(parent.length > 0 && this.element.length > 0) {
                    parent.append(this.element);
                }
            };
            Base.prototype.getEl = function () {
                return this.element;
            };
            return Base;
        })();
        ui.Base = Base;        
    })(LiveEdit.ui || (LiveEdit.ui = {}));
    var ui = LiveEdit.ui;
})(LiveEdit || (LiveEdit = {}));
var LiveEdit;
(function (LiveEdit) {
    (function (ui) {
        var $ = $liveedit;
        var HtmlElementReplacer = (function (_super) {
            __extends(HtmlElementReplacer, _super);
            function HtmlElementReplacer() {
                        _super.call(this);
                this.elementsToReplace = [
                    'iframe', 
                    'object'
                ];
                this.replaceElementsWithPlaceholders();
                console.log('HtmlElementReplacer instantiated. Using jQuery ' + $().jquery);
            }
            HtmlElementReplacer.prototype.registerGlobalListeners = function () {
            };
            HtmlElementReplacer.prototype.replaceElementsWithPlaceholders = function () {
                var me = this;
                me.getElements().each(function () {
                    me.replace($(this));
                });
            };
            HtmlElementReplacer.prototype.replace = function ($element) {
                this.hideElement($element);
                this.addPlaceholder($element);
            };
            HtmlElementReplacer.prototype.addPlaceholder = function ($element) {
                this.createPlaceholder($element).insertAfter($element);
            };
            HtmlElementReplacer.prototype.createPlaceholder = function ($element) {
                var me = this;
                var $placeholder = $('<div></div>');
                $placeholder.addClass('live-edit-html-element-placeholder');
                $placeholder.width(me.getElementWidth($element));
                $placeholder.height(me.getElementHeight($element));
                var $icon = $('<div/>');
                $icon.addClass(me.resolveIconCssClass($element));
                $icon.append('<div>' + $element[0].tagName.toLowerCase() + '</div>');
                $placeholder.append($icon);
                return $placeholder;
            };
            HtmlElementReplacer.prototype.getElements = function () {
                return $('[data-live-edit-type=part] > ' + this.elementsToReplace.toString());
            };
            HtmlElementReplacer.prototype.getElementWidth = function ($element) {
                var attrWidth = $element.attr('width');
                if(!attrWidth) {
                    return $element.width() - 2;
                }
                return attrWidth;
            };
            HtmlElementReplacer.prototype.getElementHeight = function ($element) {
                var attrHeight = $element.attr('height');
                if(!attrHeight) {
                    return $element.height() - 2;
                }
                return attrHeight;
            };
            HtmlElementReplacer.prototype.showElement = function ($element) {
                $element.show();
            };
            HtmlElementReplacer.prototype.hideElement = function ($element) {
                $element.hide();
            };
            HtmlElementReplacer.prototype.resolveIconCssClass = function ($element) {
                var tagName = $element[0].tagName.toLowerCase();
                var clsName = '';
                if(tagName === 'iframe') {
                    clsName = 'live-edit-iframe';
                } else {
                    clsName = 'live-edit-object';
                }
                return clsName;
            };
            return HtmlElementReplacer;
        })(LiveEdit.ui.Base);
        ui.HtmlElementReplacer = HtmlElementReplacer;        
    })(LiveEdit.ui || (LiveEdit.ui = {}));
    var ui = LiveEdit.ui;
})(LiveEdit || (LiveEdit = {}));
var LiveEdit;
(function (LiveEdit) {
    (function (ui) {
        var $ = $liveedit;
        var Editor = (function (_super) {
            __extends(Editor, _super);
            function Editor() {
                        _super.call(this);
                this.toolbar = new LiveEdit.ui.EditorToolbar();
                this.registerGlobalListeners();
                console.log('Editor instantiated. Using jQuery ' + $().jquery);
            }
            Editor.prototype.registerGlobalListeners = function () {
                var me = this;
                $(window).on('component.onParagraphEdit', function (event, $paragraph) {
                    me.activate($paragraph);
                });
                $(window).on('component.onParagraphEditLeave', function (event, $paragraph) {
                    me.deActivate($paragraph);
                });
                $(window).on('editorToolbar.onButtonClick', function (event, tag) {
                    document.execCommand(tag, false, null);
                });
            };
            Editor.prototype.activate = function ($paragraph) {
                $paragraph.get(0).contentEditable = true;
                $paragraph.get(0).focus();
            };
            Editor.prototype.deActivate = function ($paragraph) {
                $paragraph.get(0).contentEditable = false;
                $paragraph.get(0).blur();
            };
            return Editor;
        })(LiveEdit.ui.Base);
        ui.Editor = Editor;        
    })(LiveEdit.ui || (LiveEdit.ui = {}));
    var ui = LiveEdit.ui;
})(LiveEdit || (LiveEdit = {}));
var LiveEdit;
(function (LiveEdit) {
    (function (ui) {
        var $ = $liveedit;
        var componentHelper = LiveEdit.ComponentHelper;
        var EditorToolbar = (function (_super) {
            __extends(EditorToolbar, _super);
            function EditorToolbar() {
                        _super.call(this);
                this.selectedComponent = null;
                var me = this;
                me.selectedComponent = null;
                me.addView();
                me.addEvents();
                me.registerGlobalListeners();
                console.log('EditorToolbar instantiated. Using jQuery ' + $().jquery);
            }
            EditorToolbar.prototype.registerGlobalListeners = function () {
                $(window).on('component.onParagraphEdit', $.proxy(this.show, this));
                $(window).on('component.onParagraphEditLeave', $.proxy(this.hide, this));
                $(window).on('component.onRemove', $.proxy(this.hide, this));
                $(window).on('component.onSortStart', $.proxy(this.hide, this));
            };
            EditorToolbar.prototype.addView = function () {
                var me = this;
                var html = '<div class="live-edit-editor-toolbar live-edit-arrow-bottom" style="display: none">' + '    <button data-tag="paste" class="live-edit-editor-button"></button>' + '    <button data-tag="insertUnorderedList" class="live-edit-editor-button"></button>' + '    <button data-tag="insertOrderedList" class="live-edit-editor-button"></button>' + '    <button data-tag="link" class="live-edit-editor-button"></button>' + '    <button data-tag="cut" class="live-edit-editor-button"></button>' + '    <button data-tag="strikeThrough" class="live-edit-editor-button"></button>' + '    <button data-tag="bold" class="live-edit-editor-button"></button>' + '    <button data-tag="underline" class="live-edit-editor-button"></button>' + '    <button data-tag="italic" class="live-edit-editor-button"></button>' + '    <button data-tag="superscript" class="live-edit-editor-button"></button>' + '    <button data-tag="subscript" class="live-edit-editor-button"></button>' + '    <button data-tag="justifyLeft" class="live-edit-editor-button"></button>' + '    <button data-tag="justifyCenter" class="live-edit-editor-button"></button>' + '    <button data-tag="justifyRight" class="live-edit-editor-button"></button>' + '    <button data-tag="justifyFull" class="live-edit-editor-button"></button>' + '</div>';
                me.createElement(html);
                me.appendTo($('body'));
            };
            EditorToolbar.prototype.addEvents = function () {
                var me = this;
                me.getEl().on('click', function (event) {
                    event.stopPropagation();
                    var tag = event.target.getAttribute('data-tag');
                    if(tag) {
                        $(window).trigger('editorToolbar.onButtonClick', [
                            tag
                        ]);
                    }
                });
                $(window).scroll(function () {
                    if(me.selectedComponent) {
                        me.updatePosition();
                    }
                });
            };
            EditorToolbar.prototype.show = function (event, $component) {
                var me = this;
                me.selectedComponent = $component;
                me.getEl().show();
                me.toggleArrowPosition(false);
                me.updatePosition();
            };
            EditorToolbar.prototype.hide = function () {
                var me = this;
                me.selectedComponent = null;
                me.getEl().hide();
            };
            EditorToolbar.prototype.updatePosition = function () {
                var me = this;
                if(!me.selectedComponent) {
                    return;
                }
                var defaultPosition = me.getDefaultPosition();
                var stick = $(window).scrollTop() >= me.selectedComponent.offset().top - 60;
                if(stick) {
                    me.getEl().css({
                        position: 'fixed',
                        top: 10,
                        left: defaultPosition.left
                    });
                } else {
                    me.getEl().css({
                        position: 'absolute',
                        top: defaultPosition.top,
                        left: defaultPosition.left
                    });
                }
                var placeArrowOnTop = $(window).scrollTop() >= defaultPosition.bottom - 10;
                me.toggleArrowPosition(placeArrowOnTop);
            };
            EditorToolbar.prototype.toggleArrowPosition = function (showArrowAtTop) {
                var me = this;
                if(showArrowAtTop) {
                    me.getEl().removeClass('live-edit-arrow-bottom').addClass('live-edit-arrow-top');
                } else {
                    me.getEl().removeClass('live-edit-arrow-top').addClass('live-edit-arrow-bottom');
                }
            };
            EditorToolbar.prototype.getDefaultPosition = function () {
                var me = this;
                var componentBox = componentHelper.getBoxModel(me.selectedComponent), leftPos = componentBox.left + (componentBox.width / 2 - me.getEl().outerWidth() / 2), topPos = componentBox.top - me.getEl().height() - 25;
                return {
                    left: leftPos,
                    top: topPos,
                    bottom: componentBox.top + componentBox.height
                };
            };
            return EditorToolbar;
        })(LiveEdit.ui.Base);
        ui.EditorToolbar = EditorToolbar;        
    })(LiveEdit.ui || (LiveEdit.ui = {}));
    var ui = LiveEdit.ui;
})(LiveEdit || (LiveEdit = {}));
var LiveEdit;
(function (LiveEdit) {
    (function (ui) {
        var $ = $liveedit;
        var componentHelper = LiveEdit.ComponentHelper;
        var Shader = (function (_super) {
            __extends(Shader, _super);
            function Shader() {
                        _super.call(this);
                this.selectedComponent = null;
                this.addView();
                this.addEvents();
                this.registerGlobalListeners();
                console.log('Shader instantiated. Using jQuery ' + $().jquery);
            }
            Shader.prototype.registerGlobalListeners = function () {
                $(window).on('component.onSelect', $.proxy(this.show, this));
                $(window).on('component.onDeselect', $.proxy(this.hide, this));
                $(window).on('component.onRemove', $.proxy(this.hide, this));
                $(window).on('component.onSortStart', $.proxy(this.hide, this));
                $(window).on('component.onParagraphEdit', $.proxy(this.show, this));
                $(window).on('liveEdit.onWindowResize', $.proxy(this.handleWindowResize, this));
            };
            Shader.prototype.addView = function () {
                var $body = $('body');
                this.$pageShader = $body.append('<div class="live-edit-shader" id="live-edit-page-shader"/>');
                this.$northShader = $('<div id="live-edit-shader-north" class="live-edit-shader"/>');
                $body.append(this.$northShader);
                this.$eastShader = $('<div id="live-edit-shader-east" class="live-edit-shader"/>');
                $body.append(this.$eastShader);
                this.$southShader = $('<div id="live-edit-shader-south" class="live-edit-shader"/>');
                $body.append(this.$southShader);
                this.$westShader = $('<div id="live-edit-shader-west" class="live-edit-shader"/>');
                $body.append(this.$westShader);
            };
            Shader.prototype.addEvents = function () {
                $('.live-edit-shader').on('click contextmenu', function (event) {
                    event.stopPropagation();
                    event.preventDefault();
                    $(window).trigger('component.onDeselect');
                    $(window).trigger('shader.onClick');
                });
            };
            Shader.prototype.show = function (event, $component) {
                var me = this;
                me.selectedComponent = $component;
                if(componentHelper.getComponentType($component) === 'page') {
                    me.showForPage($component);
                } else {
                    me.showForComponent($component);
                }
            };
            Shader.prototype.showForPage = function ($component) {
                this.hide();
                $('#live-edit-page-shader').css({
                    top: 0,
                    right: 0,
                    bottom: 0,
                    left: 0
                }).show();
            };
            Shader.prototype.showForComponent = function ($component) {
                var me = this;
                $('.live-edit-shader').addClass('live-edit-animatable');
                var documentSize = LiveEdit.DomHelper.getDocumentSize(), docWidth = documentSize.width, docHeight = documentSize.height;
                var boxModel = componentHelper.getBoxModel($component), x = boxModel.left, y = boxModel.top, w = boxModel.width, h = boxModel.height;
                me.$northShader.css({
                    top: 0,
                    left: 0,
                    width: docWidth,
                    height: y
                }).show();
                me.$eastShader.css({
                    top: y,
                    left: x + w,
                    width: docWidth - (x + w),
                    height: h
                }).show();
                me.$southShader.css({
                    top: y + h,
                    left: 0,
                    width: docWidth,
                    height: docHeight - (y + h)
                }).show();
                me.$westShader.css({
                    top: y,
                    left: 0,
                    width: x,
                    height: h
                }).show();
            };
            Shader.prototype.hide = function () {
                this.selectedComponent = null;
                var $shaders = $('.live-edit-shader');
                $shaders.removeClass('live-edit-animatable');
                $shaders.hide();
            };
            Shader.prototype.handleWindowResize = function (event) {
                if(this.selectedComponent) {
                    this.show(event, this.selectedComponent);
                }
            };
            return Shader;
        })(LiveEdit.ui.Base);
        ui.Shader = Shader;        
    })(LiveEdit.ui || (LiveEdit.ui = {}));
    var ui = LiveEdit.ui;
})(LiveEdit || (LiveEdit = {}));
var LiveEdit;
(function (LiveEdit) {
    (function (ui) {
        var $ = $liveedit;
        var componentHelper = LiveEdit.ComponentHelper;
        var Cursor = (function (_super) {
            __extends(Cursor, _super);
            function Cursor() {
                        _super.call(this);
                this.registerGlobalListeners();
            }
            Cursor.prototype.registerGlobalListeners = function () {
                $(window).on('component.mouseOver', $.proxy(this.updateCursor, this));
                $(window).on('component.mouseOut', $.proxy(this.resetCursor, this));
                $(window).on('component.onSelect', $.proxy(this.updateCursor, this));
            };
            Cursor.prototype.updateCursor = function (event, $component) {
                var componentType = LiveEdit.ComponentHelper.getComponentType($component);
                var $body = $('body');
                var cursor = 'default';
                switch(componentType) {
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
            };
            Cursor.prototype.resetCursor = function () {
                $('body').css('cursor', 'default');
            };
            return Cursor;
        })(LiveEdit.ui.Base);
        ui.Cursor = Cursor;        
    })(LiveEdit.ui || (LiveEdit.ui = {}));
    var ui = LiveEdit.ui;
})(LiveEdit || (LiveEdit = {}));
var LiveEdit;
(function (LiveEdit) {
    (function (ui) {
        var $ = $liveedit;
        var componentHelper = LiveEdit.ComponentHelper;
        var Highlighter = (function (_super) {
            __extends(Highlighter, _super);
            function Highlighter() {
                        _super.call(this);
                this.selectedComponent = null;
                this.addView();
                this.registerGlobalListeners();
                console.log('Highlighter instantiated. Using jQuery ' + $().jquery);
            }
            Highlighter.prototype.registerGlobalListeners = function () {
                $(window).on('component.mouseOver', $.proxy(this.componentMouseOver, this));
                $(window).on('component.mouseOut', $.proxy(this.hide, this));
                $(window).on('component.onSelect', $.proxy(this.selectComponent, this));
                $(window).on('component.onDeselect', $.proxy(this.deselect, this));
                $(window).on('component.onSortStart', $.proxy(this.hide, this));
                $(window).on('component.onRemove', $.proxy(this.hide, this));
                $(window).on('component.onParagraphEdit', $.proxy(this.hide, this));
                $(window).on('liveEdit.onWindowResize', $.proxy(this.handleWindowResize, this));
                $(window).on('component.onSortStop', function (event, uiEvent, ui, wasSelectedOnDragStart) {
                    if(wasSelectedOnDragStart) {
                        $(window).trigger('component.onSelect', [
                            ui.item
                        ]);
                    }
                });
            };
            Highlighter.prototype.addView = function () {
                var html = '<svg xmlns="http://www.w3.org/2000/svg" version="1.1" class="live-edit-highlight-border" style="top:-5000px;left:-5000px">' + '    <rect width="150" height="150"/>' + '</svg>';
                this.createElement(html);
                this.appendTo($('body'));
            };
            Highlighter.prototype.componentMouseOver = function (event, component) {
                this.show();
                this.paintBorder(component);
            };
            Highlighter.prototype.selectComponent = function (event, component) {
                this.selectedComponent = component;
                var componentType = componentHelper.getComponentType(component);
                $('.live-edit-selected-component').removeClass('live-edit-selected-component');
                component.addClass('live-edit-selected-component');
                this.getEl().attr('class', this.getEl().attr('class') + ' live-edit-animatable');
                if(componentType === 'page') {
                    this.hide();
                    return;
                }
                this.paintBorder(component);
                this.show();
            };
            Highlighter.prototype.deselect = function () {
                this.getEl().attr('class', this.getEl().attr('class').replace(/ live-edit-animatable/g, ''));
                $('.live-edit-selected-component').removeClass('live-edit-selected-component');
                this.selectedComponent = null;
            };
            Highlighter.prototype.paintBorder = function (component) {
                var border = this.getEl();
                this.resizeBorderToComponent(component);
                var style = this.getStyleForComponent(component);
                border.css('stroke', style.strokeColor);
                border.css('fill', style.fillColor);
                border.css('stroke-dasharray', style.strokeDashArray);
            };
            Highlighter.prototype.resizeBorderToComponent = function (component) {
                var me = this;
                var componentType = componentHelper.getComponentType(component);
                var componentTagName = componentHelper.getTagNameForComponent(component);
                var componentBoxModel = componentHelper.getBoxModel(component);
                var w = Math.round(componentBoxModel.width);
                var h = Math.round(componentBoxModel.height);
                var top = Math.round(componentBoxModel.top);
                var left = Math.round(componentBoxModel.left);
                var $highlighter = me.getEl();
                var $HighlighterRect = $highlighter.find('rect');
                $highlighter.width(w);
                $highlighter.height(h);
                $HighlighterRect.attr('width', w);
                $HighlighterRect.attr('height', h);
                $highlighter.css({
                    top: top,
                    left: left
                });
            };
            Highlighter.prototype.show = function () {
                this.getEl().show();
            };
            Highlighter.prototype.hide = function () {
                this.getEl().hide();
                var $el = this.getEl();
                $el.attr('class', $el.attr('class').replace(/ live-edit-animatable/g, ''));
            };
            Highlighter.prototype.getStyleForComponent = function (component) {
                var componentType = componentHelper.getComponentType(component);
                var strokeColor, strokeDashArray, fillColor;
                switch(componentType) {
                    case 'region':
                        strokeColor = 'rgba(20,20,20,1)';
                        strokeDashArray = '';
                        fillColor = 'rgba(255,255,255,0)';
                        break;
                    case 'layout':
                        strokeColor = 'rgba(255,165,0,1)';
                        strokeDashArray = '5 5';
                        fillColor = 'rgba(100,12,36,0)';
                        break;
                    case 'part':
                        strokeColor = 'rgba(68,68,68,1)';
                        strokeDashArray = '5 5';
                        fillColor = 'rgba(255,255,255,0)';
                        break;
                    case 'paragraph':
                        strokeColor = 'rgba(85,85,255,1)';
                        strokeDashArray = '5 5';
                        fillColor = 'rgba(255,255,255,0)';
                        break;
                    case 'content':
                        strokeColor = '';
                        strokeDashArray = '';
                        fillColor = 'rgba(0,108,255,.25)';
                        break;
                    default:
                        strokeColor = 'rgba(20,20,20,1)';
                        strokeDashArray = '';
                        fillColor = 'rgba(255,255,255,0)';
                }
                return {
                    strokeColor: strokeColor,
                    strokeDashArray: strokeDashArray,
                    fillColor: fillColor
                };
            };
            Highlighter.prototype.handleWindowResize = function (event) {
                if(this.selectedComponent) {
                    this.paintBorder(this.selectedComponent);
                }
            };
            return Highlighter;
        })(LiveEdit.ui.Base);
        ui.Highlighter = Highlighter;        
    })(LiveEdit.ui || (LiveEdit.ui = {}));
    var ui = LiveEdit.ui;
})(LiveEdit || (LiveEdit = {}));
var LiveEdit;
(function (LiveEdit) {
    (function (ui) {
        var $ = $liveedit;
        var componentHelper = LiveEdit.ComponentHelper;
        var ToolTip = (function (_super) {
            __extends(ToolTip, _super);
            function ToolTip() {
                        _super.call(this);
                this.OFFSET_X = 0;
                this.OFFSET_Y = 18;
                this.addView();
                this.attachEventListeners();
                this.registerGlobalListeners();
                console.log('ToolTip instantiated. Using jQuery ' + $().jquery);
            }
            ToolTip.prototype.registerGlobalListeners = function () {
                $(window).on('component.onSelect', $.proxy(this.hide, this));
            };
            ToolTip.prototype.addView = function () {
                var me = this;
                var html = '<div class="live-edit-tool-tip" style="top:-5000px; left:-5000px;">' + '    <span class="live-edit-tool-tip-name-text"></span>' + '    <span class="live-edit-tool-tip-type-text"></span> ' + '</div>';
                me.createElement(html);
                me.appendTo($('body'));
            };
            ToolTip.prototype.setText = function (componentType, componentName) {
                var $tooltip = this.getEl();
                $tooltip.children('.live-edit-tool-tip-type-text').text(componentType);
                $tooltip.children('.live-edit-tool-tip-name-text').text(componentName);
            };
            ToolTip.prototype.attachEventListeners = function () {
                var me = this;
                $(document).on('mousemove', '[data-live-edit-type]', function (event) {
                    var targetIsUiComponent = $(event.target).is('[id*=live-edit-ui-cmp]') || $(event.target).parents('[id*=live-edit-ui-cmp]').length > 0;
                    var pageHasComponentSelected = $('.live-edit-selected-component').length > 0;
                    if(targetIsUiComponent || pageHasComponentSelected || AdminLiveEdit.DragDropSort.isDragging()) {
                        me.hide();
                        return;
                    }
                    var $component = $(event.target).closest('[data-live-edit-type]');
                    var componentInfo = componentHelper.getComponentInfo($component);
                    var pos = me.getPosition(event);
                    me.getEl().css({
                        top: pos.y,
                        left: pos.x
                    });
                    me.setText(componentInfo.type, componentInfo.name);
                });
                $(document).on('hover', '[data-live-edit-type]', function (event) {
                    if(event.type === 'mouseenter') {
                        me.getEl().hide().fadeIn(300);
                    }
                });
                $(document).on('mouseout', function () {
                    me.hide.call(me);
                });
            };
            ToolTip.prototype.getPosition = function (event) {
                var t = this;
                var domHelper = LiveEdit.DomHelper;
                var pageX = event.pageX;
                var pageY = event.pageY;
                var x = pageX + t.OFFSET_X;
                var y = pageY + t.OFFSET_Y;
                var viewPortSize = domHelper.getViewPortSize();
                var scrollTop = domHelper.getDocumentScrollTop();
                var toolTipWidth = t.getEl().width();
                var toolTipHeight = t.getEl().height();
                if(x + toolTipWidth > (viewPortSize.width - t.OFFSET_X * 2) - 50) {
                    x = pageX - toolTipWidth - (t.OFFSET_X * 2);
                }
                if(y + toolTipHeight > (viewPortSize.height + scrollTop - t.OFFSET_Y * 2)) {
                    y = pageY - toolTipHeight - (t.OFFSET_Y * 2);
                }
                return {
                    x: x,
                    y: y
                };
            };
            ToolTip.prototype.hide = function () {
                this.getEl().css({
                    top: '-5000px',
                    left: '-5000px'
                });
            };
            return ToolTip;
        })(LiveEdit.ui.Base);
        ui.ToolTip = ToolTip;        
    })(LiveEdit.ui || (LiveEdit.ui = {}));
    var ui = LiveEdit.ui;
})(LiveEdit || (LiveEdit = {}));
var LiveEdit;
(function (LiveEdit) {
    (function (ui) {
        var $ = $liveedit;
        var componentHelper = LiveEdit.ComponentHelper;
        var Menu = (function (_super) {
            __extends(Menu, _super);
            function Menu() {
                        _super.call(this);
                this.previousPageSizes = null;
                this.previousPagePositions = null;
                this.hidden = true;
                this.buttons = [];
                this.buttonConfig = {
                    'page': [
                        'settings', 
                        'reset'
                    ],
                    'region': [
                        'parent', 
                        'settings', 
                        'reset', 
                        'clear'
                    ],
                    'layout': [
                        'parent', 
                        'settings', 
                        'remove'
                    ],
                    'part': [
                        'parent', 
                        'settings', 
                        'details', 
                        'remove'
                    ],
                    'content': [
                        'parent', 
                        'opencontent', 
                        'view'
                    ],
                    'paragraph': [
                        'parent', 
                        'edit', 
                        'remove'
                    ]
                };
                this.addView();
                this.registerEvents();
                this.registerGlobalListeners();
                console.log('Menu instantiated. Using jQuery ' + $().jquery);
            }
            Menu.prototype.registerGlobalListeners = function () {
                $(window).on('component.onSelect', $.proxy(this.show, this));
                $(window).on('component.onDeselect', $.proxy(this.hide, this));
                $(window).on('component.onSortStart', $.proxy(this.fadeOutAndHide, this));
                $(window).on('component.onRemove', $.proxy(this.hide, this));
                $(window).on('component.onParagraphEdit', $.proxy(this.hide, this));
            };
            Menu.prototype.addView = function () {
                var html = '';
                html += '<div class="live-edit-component-menu live-edit-arrow-top" style="display: none">';
                html += '   <div class="live-edit-component-menu-title-bar">';
                html += '       <div class="live-edit-component-menu-title-icon"><div><!-- --></div></div>';
                html += '       <div class="live-edit-component-menu-title-text"><!-- populated --></div>';
                html += '       <div class="live-edit-component-menu-title-close-button"><!-- --></div>';
                html += '   </div>';
                html += '   <div class="live-edit-component-menu-items">';
                html += '   </div>';
                html += '</div>';
                this.createElement(html);
                this.appendTo($('body'));
                this.addButtons();
            };
            Menu.prototype.registerEvents = function () {
                var me = this;
                me.getEl().draggable({
                    handle: '.live-edit-component-menu-title-bar',
                    addClasses: false
                });
                me.getCloseButton().click(function () {
                    $(window).trigger('component.onDeselect');
                });
            };
            Menu.prototype.show = function (event, $component, pagePosition) {
                var me = this;
                me.selectedComponent = $component;
                me.previousPagePositions = pagePosition;
                me.previousPageSizes = LiveEdit.DomHelper.getViewPortSize();
                me.updateTitleBar($component);
                me.updateMenuItemsForComponent($component);
                var pageXPosition = pagePosition.x - me.getEl().width() / 2, pageYPosition = pagePosition.y + 15;
                me.moveToXY(pageXPosition, pageYPosition);
                me.getEl().show();
                this.hidden = false;
            };
            Menu.prototype.hide = function () {
                this.selectedComponent = null;
                this.getEl().hide();
                this.hidden = true;
            };
            Menu.prototype.fadeOutAndHide = function () {
                var me = this;
                me.getEl().fadeOut(500, function () {
                    me.hide();
                    $(window).trigger('component.onDeselect', {
                        showComponentBar: false
                    });
                });
                me.selectedComponent = null;
            };
            Menu.prototype.moveToXY = function (x, y) {
                this.getEl().css({
                    left: x,
                    top: y
                });
            };
            Menu.prototype.addButtons = function () {
                var me = this;
                var parentButton = new LiveEdit.ui.ParentButton(me);
                var settingsButton = new LiveEdit.ui.SettingsButton(me);
                var detailsButton = new LiveEdit.ui.DetailsButton(me);
                var insertButton = new LiveEdit.ui.InsertButton(me);
                var resetButton = new LiveEdit.ui.ResetButton(me);
                var clearButton = new LiveEdit.ui.ClearButton(me);
                var openContentButton = new LiveEdit.ui.OpenContentButton(me);
                var viewButton = new LiveEdit.ui.ViewButton(me);
                var editButton = new LiveEdit.ui.EditButton(me);
                var removeButton = new LiveEdit.ui.RemoveButton(me);
                var i, $menuItemsPlaceholder = me.getMenuItemsPlaceholderElement();
                for(i = 0; i < me.buttons.length; i++) {
                    me.buttons[i].appendTo($menuItemsPlaceholder);
                }
            };
            Menu.prototype.updateMenuItemsForComponent = function ($component) {
                var componentType = componentHelper.getComponentType($component);
                var buttonArray = this.getConfigForButton(componentType);
                var buttons = this.getButtons();
                var i;
                for(i = 0; i < buttons.length; i++) {
                    var $button = buttons[i].getEl();
                    var id = $button.attr('data-live-edit-ui-cmp-id');
                    var subStr = id.substring(id.lastIndexOf('-') + 1, id.length);
                    if(buttonArray.indexOf(subStr) > -1) {
                        $button.show();
                    } else {
                        $button.hide();
                    }
                }
            };
            Menu.prototype.updateTitleBar = function ($component) {
                var componentInfo = componentHelper.getComponentInfo($component);
                this.setIcon(componentInfo.type);
                this.setTitle(componentInfo.name);
            };
            Menu.prototype.setTitle = function (titleText) {
                this.getTitleElement().text(titleText);
            };
            Menu.prototype.setIcon = function (componentType) {
                var $iconCt = this.getIconElement(), iconCls = this.resolveCssClassForComponentType(componentType);
                $iconCt.children('div').attr('class', iconCls);
                $iconCt.attr('title', componentType);
            };
            Menu.prototype.resolveCssClassForComponentType = function (componentType) {
                var iconCls;
                switch(componentType) {
                    case 'page':
                        iconCls = 'live-edit-component-menu-page-icon';
                        break;
                    case 'region':
                        iconCls = 'live-edit-component-menu-region-icon';
                        break;
                    case 'layout':
                        iconCls = 'live-edit-component-menu-layout-icon';
                        break;
                    case 'part':
                        iconCls = 'live-edit-component-menu-part-icon';
                        break;
                    case 'content':
                        iconCls = 'live-edit-component-menu-content-icon';
                        break;
                    case 'paragraph':
                        iconCls = 'live-edit-component-menu-paragraph-icon';
                        break;
                    default:
                        iconCls = '';
                }
                return iconCls;
            };
            Menu.prototype.getButtons = function () {
                return this.buttons;
            };
            Menu.prototype.getConfigForButton = function (componentType) {
                return this.buttonConfig[componentType];
            };
            Menu.prototype.getIconElement = function () {
                return $('.live-edit-component-menu-title-icon', this.getEl());
            };
            Menu.prototype.getTitleElement = function () {
                return $('.live-edit-component-menu-title-text', this.getEl());
            };
            Menu.prototype.getCloseButton = function () {
                return $('.live-edit-component-menu-title-close-button', this.getEl());
            };
            Menu.prototype.getMenuItemsPlaceholderElement = function () {
                return $('.live-edit-component-menu-items', this.getEl());
            };
            Menu.prototype.handleWindowResize = function (event) {
                if(this.selectedComponent) {
                    var x = this.previousPagePositions.x, y = this.previousPagePositions.y;
                    x = x - (this.previousPageSizes.width - LiveEdit.DomHelper.getViewPortSize().width);
                    this.moveToXY(x, y);
                }
            };
            return Menu;
        })(LiveEdit.ui.Base);
        ui.Menu = Menu;        
    })(LiveEdit.ui || (LiveEdit.ui = {}));
    var ui = LiveEdit.ui;
})(LiveEdit || (LiveEdit = {}));
var LiveEdit;
(function (LiveEdit) {
    (function (ui) {
        var $ = $liveedit;
        var BaseButton = (function (_super) {
            __extends(BaseButton, _super);
            function BaseButton() {
                        _super.call(this);
            }
            BaseButton.prototype.createButton = function (config) {
                var id = config.id || '';
                var text = config.text || '';
                var cls = config.cls || '';
                var iconCls = config.iconCls || '';
                var html = '<div data-live-edit-ui-cmp-id="' + id + '" class="live-edit-button ' + cls + '">';
                if(iconCls !== '') {
                    html += '<span class="live-edit-button-icon ' + iconCls + '"></span>';
                }
                html += '<span class="live-edit-button-text">' + text + '</span></div>';
                var $button = this.createElement(html);
                if(config.handler) {
                    $button.on('click', function (event) {
                        config.handler.call(this, event);
                    });
                }
                return $button;
            };
            return BaseButton;
        })(LiveEdit.ui.Base);
        ui.BaseButton = BaseButton;        
    })(LiveEdit.ui || (LiveEdit.ui = {}));
    var ui = LiveEdit.ui;
})(LiveEdit || (LiveEdit = {}));
var LiveEdit;
(function (LiveEdit) {
    (function (ui) {
        var $ = $liveedit;
        var componentHelper = LiveEdit.ComponentHelper;
        var ParentButton = (function (_super) {
            __extends(ParentButton, _super);
            function ParentButton(menu) {
                        _super.call(this);
                this.menu = null;
                this.menu = menu;
                this.init();
            }
            ParentButton.prototype.init = function () {
                var me = this;
                var $button = me.createButton({
                    id: 'live-edit-button-parent',
                    text: 'Select Parent',
                    cls: 'live-edit-component-menu-button',
                    handler: function (event) {
                        event.stopPropagation();
                        var $parent = me.menu.selectedComponent.parents('[data-live-edit-type]');
                        if($parent && $parent.length > 0) {
                            $parent = $($parent[0]);
                            $(window).trigger('component.onSelect', [
                                $parent, 
                                {
                                    x: 0,
                                    y: 0
                                }
                            ]);
                            me.scrollComponentIntoView($parent);
                            var menuWidth = me.menu.getEl().outerWidth();
                            var componentBox = componentHelper.getBoxModel($parent), newMenuPosition = {
                                x: componentBox.left + (componentBox.width / 2) - (menuWidth / 2),
                                y: componentBox.top + 10
                            };
                            me.menu.moveToXY(newMenuPosition.x, newMenuPosition.y);
                        }
                    }
                });
                me.appendTo(this.menu.getEl());
                me.menu.buttons.push(me);
            };
            ParentButton.prototype.scrollComponentIntoView = function ($component) {
                var componentTopPosition = componentHelper.getPagePositionForComponent($component).top;
                if(componentTopPosition <= window.pageYOffset) {
                    $('html, body').animate({
                        scrollTop: componentTopPosition - 10
                    }, 200);
                }
            };
            return ParentButton;
        })(LiveEdit.ui.BaseButton);
        ui.ParentButton = ParentButton;        
    })(LiveEdit.ui || (LiveEdit.ui = {}));
    var ui = LiveEdit.ui;
})(LiveEdit || (LiveEdit = {}));
var LiveEdit;
(function (LiveEdit) {
    (function (ui) {
        var $ = $liveedit;
        var OpenContentButton = (function (_super) {
            __extends(OpenContentButton, _super);
            function OpenContentButton(menu) {
                        _super.call(this);
                this.menu = null;
                this.menu = menu;
                this.init();
            }
            OpenContentButton.prototype.init = function () {
                var me = this;
                var $button = me.createButton({
                    text: 'Open in new tab',
                    id: 'live-edit-button-opencontent',
                    cls: 'live-edit-component-menu-button',
                    handler: function (event) {
                        event.stopPropagation();
                        var parentWindow = window['parent'];
                        if(parentWindow && parentWindow['Admin'].MessageBus) {
                            parentWindow['Admin'].MessageBus.liveEditOpenContent();
                        }
                    }
                });
                me.appendTo(me.menu.getEl());
                me.menu.buttons.push(me);
            };
            return OpenContentButton;
        })(LiveEdit.ui.BaseButton);
        ui.OpenContentButton = OpenContentButton;        
    })(LiveEdit.ui || (LiveEdit.ui = {}));
    var ui = LiveEdit.ui;
})(LiveEdit || (LiveEdit = {}));
var LiveEdit;
(function (LiveEdit) {
    (function (ui) {
        var $ = $liveedit;
        var InsertButton = (function (_super) {
            __extends(InsertButton, _super);
            function InsertButton(menu) {
                        _super.call(this);
                this.menu = null;
                this.menu = menu;
                this.init();
            }
            InsertButton.prototype.init = function () {
                var me = this;
                var $button = me.createButton({
                    text: 'Insert',
                    id: 'live-edit-button-insert',
                    cls: 'live-edit-component-menu-button',
                    handler: function (event) {
                        event.stopPropagation();
                    }
                });
                me.appendTo(me.menu.getEl());
                me.menu.buttons.push(me);
            };
            return InsertButton;
        })(LiveEdit.ui.BaseButton);
        ui.InsertButton = InsertButton;        
    })(LiveEdit.ui || (LiveEdit.ui = {}));
    var ui = LiveEdit.ui;
})(LiveEdit || (LiveEdit = {}));
var LiveEdit;
(function (LiveEdit) {
    (function (ui) {
        var $ = $liveedit;
        var DetailsButton = (function (_super) {
            __extends(DetailsButton, _super);
            function DetailsButton(menu) {
                        _super.call(this);
                this.menu = null;
                this.menu = menu;
                this.init();
            }
            DetailsButton.prototype.init = function () {
                var me = this;
                var $button = me.createButton({
                    text: 'Show Details',
                    id: 'live-edit-button-details',
                    cls: 'live-edit-component-menu-button',
                    handler: function (event) {
                        event.stopPropagation();
                    }
                });
                me.appendTo(me.menu.getEl());
                me.menu.buttons.push(me);
            };
            return DetailsButton;
        })(LiveEdit.ui.BaseButton);
        ui.DetailsButton = DetailsButton;        
    })(LiveEdit.ui || (LiveEdit.ui = {}));
    var ui = LiveEdit.ui;
})(LiveEdit || (LiveEdit = {}));
var LiveEdit;
(function (LiveEdit) {
    (function (ui) {
        var $ = $liveedit;
        var EditButton = (function (_super) {
            __extends(EditButton, _super);
            function EditButton(menu) {
                        _super.call(this);
                this.menu = null;
                this.menu = menu;
                this.init();
            }
            EditButton.prototype.init = function () {
                var me = this;
                var $button = me.createButton({
                    id: 'live-edit-button-edit',
                    text: 'Edit',
                    cls: 'live-edit-component-menu-button',
                    handler: function (event) {
                        event.stopPropagation();
                        var $paragraph = me.menu.selectedComponent;
                        if($paragraph && $paragraph.length > 0) {
                            $(window).trigger('component.onParagraphEdit', [
                                $paragraph
                            ]);
                        }
                    }
                });
                me.appendTo(me.menu.getEl());
                me.menu.buttons.push(me);
            };
            return EditButton;
        })(LiveEdit.ui.BaseButton);
        ui.EditButton = EditButton;        
    })(LiveEdit.ui || (LiveEdit.ui = {}));
    var ui = LiveEdit.ui;
})(LiveEdit || (LiveEdit = {}));
var LiveEdit;
(function (LiveEdit) {
    (function (ui) {
        var $ = $liveedit;
        var ResetButton = (function (_super) {
            __extends(ResetButton, _super);
            function ResetButton(menu) {
                        _super.call(this);
                this.menu = null;
                this.menu = menu;
                this.init();
            }
            ResetButton.prototype.init = function () {
                var me = this;
                var $button = me.createButton({
                    text: 'Reset to Default',
                    id: 'live-edit-button-reset',
                    cls: 'live-edit-component-menu-button',
                    handler: function (event) {
                        event.stopPropagation();
                    }
                });
                me.appendTo(me.menu.getEl());
                me.menu.buttons.push(me);
            };
            return ResetButton;
        })(LiveEdit.ui.BaseButton);
        ui.ResetButton = ResetButton;        
    })(LiveEdit.ui || (LiveEdit.ui = {}));
    var ui = LiveEdit.ui;
})(LiveEdit || (LiveEdit = {}));
var LiveEdit;
(function (LiveEdit) {
    (function (ui) {
        var $ = $liveedit;
        var ClearButton = (function (_super) {
            __extends(ClearButton, _super);
            function ClearButton(menu) {
                        _super.call(this);
                this.menu = null;
                this.menu = menu;
                this.init();
            }
            ClearButton.prototype.init = function () {
                var me = this;
                var $button = me.createButton({
                    text: 'Empty',
                    id: 'live-edit-button-clear',
                    cls: 'live-edit-component-menu-button',
                    handler: function (event) {
                        event.stopPropagation();
                    }
                });
                me.appendTo(me.menu.getEl());
                me.menu.buttons.push(me);
            };
            return ClearButton;
        })(LiveEdit.ui.BaseButton);
        ui.ClearButton = ClearButton;        
    })(LiveEdit.ui || (LiveEdit.ui = {}));
    var ui = LiveEdit.ui;
})(LiveEdit || (LiveEdit = {}));
var LiveEdit;
(function (LiveEdit) {
    (function (ui) {
        var $ = $liveedit;
        var ViewButton = (function (_super) {
            __extends(ViewButton, _super);
            function ViewButton(menu) {
                        _super.call(this);
                this.menu = null;
                this.menu = menu;
                this.init();
            }
            ViewButton.prototype.init = function () {
                var me = this;
                var $button = me.createButton({
                    text: 'View',
                    id: 'live-edit-button-view',
                    cls: 'live-edit-component-menu-button',
                    handler: function (event) {
                        event.stopPropagation();
                    }
                });
                me.appendTo(me.menu.getEl());
                me.menu.buttons.push(me);
            };
            return ViewButton;
        })(LiveEdit.ui.BaseButton);
        ui.ViewButton = ViewButton;        
    })(LiveEdit.ui || (LiveEdit.ui = {}));
    var ui = LiveEdit.ui;
})(LiveEdit || (LiveEdit = {}));
var LiveEdit;
(function (LiveEdit) {
    (function (ui) {
        var $ = $liveedit;
        var SettingsButton = (function (_super) {
            __extends(SettingsButton, _super);
            function SettingsButton(menu) {
                        _super.call(this);
                this.menu = null;
                this.menu = menu;
                this.init();
            }
            SettingsButton.prototype.init = function () {
                var me = this;
                var $button = me.createButton({
                    text: 'Settings',
                    id: 'live-edit-button-settings',
                    cls: 'live-edit-component-menu-button',
                    handler: function (event) {
                        event.stopPropagation();
                        var parentWindow = window['parent'];
                        if(parentWindow && parentWindow['Admin'].MessageBus) {
                            parentWindow['Admin'].MessageBus.showLiveEditTestSettingsWindow({
                            });
                        }
                    }
                });
                me.appendTo(me.menu.getEl());
                me.menu.buttons.push(me);
            };
            return SettingsButton;
        })(LiveEdit.ui.BaseButton);
        ui.SettingsButton = SettingsButton;        
    })(LiveEdit.ui || (LiveEdit.ui = {}));
    var ui = LiveEdit.ui;
})(LiveEdit || (LiveEdit = {}));
var LiveEdit;
(function (LiveEdit) {
    (function (ui) {
        var $ = $liveedit;
        var RemoveButton = (function (_super) {
            __extends(RemoveButton, _super);
            function RemoveButton(menu) {
                        _super.call(this);
                this.menu = null;
                this.menu = menu;
                this.init();
            }
            RemoveButton.prototype.init = function () {
                var me = this;
                var $button = me.createButton({
                    text: 'Remove',
                    id: 'live-edit-button-remove',
                    cls: 'live-edit-component-menu-button',
                    handler: function (event) {
                        event.stopPropagation();
                        me.menu.selectedComponent.remove();
                        $(window).trigger('component.onRemove');
                    }
                });
                me.appendTo(me.menu.getEl());
                me.menu.buttons.push(me);
            };
            return RemoveButton;
        })(LiveEdit.ui.BaseButton);
        ui.RemoveButton = RemoveButton;        
    })(LiveEdit.ui || (LiveEdit.ui = {}));
    var ui = LiveEdit.ui;
})(LiveEdit || (LiveEdit = {}));
var LiveEdit;
(function (LiveEdit) {
    (function (ui) {
        var $ = $liveedit;
        var ComponentBar = (function (_super) {
            __extends(ComponentBar, _super);
            function ComponentBar() {
                        _super.call(this);
                this.BAR_WIDTH = 235;
                this.TOGGLE_WIDTH = 30;
                this.INNER_WIDTH = this.BAR_WIDTH - this.TOGGLE_WIDTH;
                this.hidden = true;
                this.addView();
                this.loadComponentsData();
                this.registerGlobalListeners();
                this.registerEvents();
                console.log('ComponentBar instantiated. Using jQuery ' + $().jquery);
            }
            ComponentBar.prototype.getComponentsDataUrl = function () {
                return '../../../admin2/live-edit/data/mock-components.json';
            };
            ComponentBar.prototype.addView = function () {
                var me = this;
                var html = '';
                html += '<div class="live-edit-components-container live-edit-collapsed" style="width:' + this.BAR_WIDTH + 'px; right: -' + this.INNER_WIDTH + 'px">';
                html += '    <div class="live-edit-toggle-components-container" style="width:' + this.TOGGLE_WIDTH + 'px"><span class="live-edit-toggle-text-container">Toolbar</span></div>';
                html += '        <div class="live-edit-components">';
                html += '            <div class="live-edit-form-container">';
                html += '               <form onsubmit="return false;">';
                html += '                   <input type="text" placeholder="Filter" name="filter"/>';
                html += '               </form>';
                html += '            </div>';
                html += '            <ul>';
                html += '            </ul>';
                html += '        </div>';
                html += '    </div>';
                html += '</div>';
                me.createElement(html);
                me.appendTo($('body'));
            };
            ComponentBar.prototype.registerGlobalListeners = function () {
                var me = this;
                $(window).on('component.onSelect', $.proxy(me.fadeOut, me));
                $(window).on('component.onDeselect', $.proxy(me.fadeIn, me));
                $(window).on('component.onDragStart', $.proxy(me.fadeOut, me));
                $(window).on('component.onDragStop', $.proxy(me.fadeIn, me));
                $(window).on('component.onSortStop', $.proxy(me.fadeIn, me));
                $(window).on('component.onSortStart', $.proxy(me.fadeOut, me));
                $(window).on('component.onSortUpdate', $.proxy(me.fadeIn, me));
                $(window).on('component.onRemove', $.proxy(me.fadeIn, me));
            };
            ComponentBar.prototype.registerEvents = function () {
                var me = this;
                me.getToggle().click(function () {
                    me.toggle();
                });
                me.getFilterInput().on('keyup', function () {
                    me.filterList($(this).val());
                });
                me.getBar().on('mouseover', function () {
                    $(window).trigger('componentBar:mouseover');
                });
            };
            ComponentBar.prototype.loadComponentsData = function () {
                var me = this;
                $.getJSON(me.getComponentsDataUrl(), null, function (data, textStatus, jqXHR) {
                    me.renderComponents(data);
                    $(window).trigger('componentBar.dataLoaded');
                });
            };
            ComponentBar.prototype.renderComponents = function (jsonData) {
                var me = this, $container = me.getComponentsContainer(), groups = jsonData.componentGroups;
                $.each(groups, function (index, group) {
                    me.addHeader(group);
                    if(group.components) {
                        me.addComponentsToGroup(group.components);
                    }
                });
            };
            ComponentBar.prototype.addHeader = function (componentGroup) {
                var me = this, html = '';
                html += '<li class="live-edit-component-list-header">';
                html += '    <span>' + componentGroup.name + '</span>';
                html += '</li>';
                me.getComponentsContainer().append(html);
            };
            ComponentBar.prototype.addComponentsToGroup = function (components) {
                var me = this;
                $.each(components, function (index, component) {
                    me.addComponent(component);
                });
            };
            ComponentBar.prototype.addComponent = function (component) {
                var me = this, html = '';
                html += '<li class="live-edit-component" data-live-edit-component-key="' + component.key + '" data-live-edit-component-name="' + component.name + '" data-live-edit-component-type="' + component.type + '">';
                html += '    <img src="' + component.icon + '"/>';
                html += '    <div class="live-edit-component-text">';
                html += '        <div class="live-edit-component-text-name">' + component.name + '</div>';
                html += '        <div class="live-edit-component-text-subtitle">' + component.subtitle + '</div>';
                html += '    </div>';
                html += '</li>';
                me.getComponentsContainer().append(html);
            };
            ComponentBar.prototype.filterList = function (value) {
                var me = this, $element, name, valueLowerCased = value.toLowerCase();
                me.getComponentList().each(function (index) {
                    $element = $(this);
                    name = $element.data('live-edit-component-name').toLowerCase();
                    $element.css('display', name.indexOf(valueLowerCased) > -1 ? '' : 'none');
                });
            };
            ComponentBar.prototype.toggle = function () {
                var me = this;
                if(me.hidden) {
                    me.show();
                    me.hidden = false;
                } else {
                    me.hide();
                    me.hidden = true;
                }
            };
            ComponentBar.prototype.show = function () {
                var me = this;
                var $bar = me.getBar();
                $bar.css('right', '0');
                me.getToggleTextContainer().text('');
                $bar.removeClass('live-edit-collapsed');
            };
            ComponentBar.prototype.hide = function () {
                var me = this;
                var $bar = me.getBar();
                $bar.css('right', '-' + this.INNER_WIDTH + 'px');
                me.getToggleTextContainer().text('Toolbar');
                $bar.addClass('live-edit-collapsed');
            };
            ComponentBar.prototype.fadeIn = function (event, triggerConfig) {
                if(triggerConfig && triggerConfig.showComponentBar === false) {
                    return;
                }
                this.getBar().fadeIn(120);
            };
            ComponentBar.prototype.fadeOut = function (event) {
                this.getBar().fadeOut(120);
            };
            ComponentBar.prototype.getBar = function () {
                return this.getEl();
            };
            ComponentBar.prototype.getToggle = function () {
                return $('.live-edit-toggle-components-container', this.getEl());
            };
            ComponentBar.prototype.getFilterInput = function () {
                return $('.live-edit-form-container input[name=filter]', this.getEl());
            };
            ComponentBar.prototype.getComponentsContainer = function () {
                return $('.live-edit-components ul', this.getEl());
            };
            ComponentBar.prototype.getComponentList = function () {
                return $('.live-edit-component', this.getEl());
            };
            ComponentBar.prototype.getToggleTextContainer = function () {
                return $('.live-edit-toggle-text-container', this.getEl());
            };
            return ComponentBar;
        })(LiveEdit.ui.Base);
        ui.ComponentBar = ComponentBar;        
    })(LiveEdit.ui || (LiveEdit.ui = {}));
    var ui = LiveEdit.ui;
})(LiveEdit || (LiveEdit = {}));
((function ($) {
    'use strict';
    $(window).load(function () {
        $('.live-edit-loader-splash-container').fadeOut('fast', function () {
            $(this).remove();
            new LiveEdit.model.Page();
            new LiveEdit.model.Region();
            new LiveEdit.model.Layout();
            new LiveEdit.model.Part();
            new LiveEdit.model.Paragraph();
            new LiveEdit.model.Content();
            new LiveEdit.ui.HtmlElementReplacer();
            new LiveEdit.ui.Highlighter();
            new LiveEdit.ui.ToolTip();
            new LiveEdit.ui.Cursor();
            new LiveEdit.ui.Menu();
            new LiveEdit.ui.Shader();
            new LiveEdit.ui.Editor();
            new LiveEdit.ui.ComponentBar();
            new LiveEdit.MutationObserver();
            AdminLiveEdit.DragDropSort.initialize();
            $(window).resize(function () {
                $(window).trigger('liveEdit.onWindowResize');
            });
        });
    });
    $(document).ready(function () {
        $(document).on('mousedown', 'btn, button, a, select', function (event) {
            event.preventDefault();
            return false;
        });
    });
})($liveedit));
//@ sourceMappingURL=all.js.map
