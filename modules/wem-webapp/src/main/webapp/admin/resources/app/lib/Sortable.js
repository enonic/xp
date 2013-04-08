/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
Ext.define('Admin.lib.Sortable', {
    constructor: function (parentComponent, config) {
        var me = this;
        me.config = config || {};
        me.parentComponent = parentComponent;
        me.id = Ext.id();
        me.group = me.config.group || '' + me.id;
        me.indicatorEl = me.createDDIndicator(config.name);
        Ext.dd.ScrollManager.register(this.parentComponent.el);
        me.initDragZone();
        me.initDropZone();
    },


    initDragZone: function () {
        var sortable = this;
        var dragZone = new Ext.dd.DragZone(sortable.parentComponent.getEl(), {
            ddGroup: sortable.group,
            containerScroll: true,

            proxy: new Ext.dd.StatusProxy({}),

            getDragData: function (e) {
                var sourceElement = e.getTarget('.admin-sortable');
                if (!sourceElement) {
                    return;
                }

                // If a handle is configured and anything else than the handle is pressed, return.
                if (sortable.config.handle && !Ext.fly(sourceElement).down(sortable.config.handle).contains(Ext.fly(e.getTarget()))) {
                    return;
                }

                return {
                    ddel: sortable.createDragProxy(sourceElement),
                    sourceElement: sourceElement,
                    repairXY: Ext.fly(sourceElement).getXY()
                };
            },

            onInitDrag: function (x, y) {
                this.proxy.update(this.dragData.ddel.cloneNode(true));
                this.onStartDrag(x, y);
                return true;
            },

            beforeDragOver: function () {
                return true;
            },

            onMouseUp: function (e) {
                Ext.fly(this.dragData.sourceElement).setStyle('opacity', '1');
            },

            afterInvalidDrop: function (e, id) {
                sortable.hideIndicator();
            },

            getRepairXY: function () {
                return this.dragData.repairXY;
            }
        });
    },


    initDropZone: function () {
        var sortable = this;

        var dropZone = new Ext.dd.DropZone(sortable.parentComponent.getEl(), {
            ddGroup: sortable.group,

            getTargetFromEvent: function (e) {
                return e.getTarget('.admin-sortable');
            },

            onNodeOver: function (target, dd, e, data) {
                var cmpNode = Ext.getCmp(target.id);
                if (!cmpNode) {
                    return;
                }
                if (target === data.sourceElement) {
                    return;
                }

                if (!cmpNode.hasCls('admin-drop-indicator')) {
                    var mouseYPos = e.getY();

                    var cmpArea = cmpNode.getEl().getPageBox();

                    var nodeMiddle = cmpArea.top + cmpArea.height / 2;
                    if (mouseYPos < nodeMiddle) {
                        sortable.currentPos = 'above';
                    } else {
                        sortable.currentPos = 'below';
                    }

                    sortable.showIndicator(cmpNode, sortable.currentPos);
                }
                return Ext.dd.DropZone.prototype.dropAllowed;
            },

            onNodeDrop: function (target, dd, e, data) {
                var draggedCmp = Ext.getCmp(data.sourceElement.id);
                var targetCmp = Ext.getCmp(target.id);

                if (target === data.sourceElement) {
                    return;
                }

                draggedCmp.getEl().setStyle('opacity', 1);

                if (targetCmp) {
                    var targetCmpIndex = sortable.getIndexOfComponent(targetCmp);
                    var draggedCmpOrgIndex = sortable.getIndexOfComponent(draggedCmp);
                    if (sortable.currentPos === 'below') {
                        targetCmpIndex = targetCmpIndex + 1;
                        if (draggedCmpOrgIndex < targetCmpIndex) {
                            targetCmpIndex = targetCmpIndex - 1;
                        }
                    }

                    sortable.parentComponent.insert(targetCmpIndex, draggedCmp);
                    sortable.parentComponent.doLayout();
                }

                sortable.hideIndicator();

                return true;
            }
        });

    },


    createDDIndicator: function (name) {
        var me = this,
            indicatorEl,
            arrowLeft;

        indicatorEl = Ext.create('widget.component', {
            html: '<div>Drop ' + name + ' here </div>',
            cls: 'admin-drop-indicator admin-sortable'
        });

        return indicatorEl;
    },


    showIndicator: function (area, position) {
        this.indicatorEl.show();
        var index = this.getIndexOfComponent(area);

        if (index > -1) {
            var insertPoint = position === 'above' ? index : index + 1;
            this.parentComponent.insert(insertPoint, this.indicatorEl);
        }

    },


    hideIndicator: function () {
        this.indicatorEl.hide();
        this.parentComponent.remove(this.indicatorEl, false);
    },


    createDragProxy: function (sourceElement) {
        var proxyEl;

        if (this.config.proxyHtml) {
            proxyEl = Ext.get(document.createElement('div'));
            proxyEl.setHTML(this.config.proxyHtml);
            proxyEl = proxyEl.dom;
        } else { // If no proxy is configured we'll clone the source element
            proxyEl = sourceElement.cloneNode(true);
        }
        return proxyEl;
    },

    getIndexOfComponent: function (cmp) {
        return this.parentComponent.items.indexOf(cmp);
    }

});