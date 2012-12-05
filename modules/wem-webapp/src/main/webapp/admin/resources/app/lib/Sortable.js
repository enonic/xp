/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
Ext.define('Admin.lib.Sortable', {
    constructor: function (parentComponent, group, config) {
        var me = this;

        me.parentComponent = parentComponent;
        me.config = config || {};

        var dragZone = new Ext.dd.DragZone(parentComponent.getEl(), {
            ddGroup: group,
            getDragData: function (e) {
                var sourceElement = e.getTarget('.admin-sortable');
                if (!sourceElement) {
                    return;
                }

                // If a handle is configured and anything else than the handle is pressed, return.
                if (me.config.handle && Ext.fly(sourceElement).down(me.config.handle).dom !== e.getTarget()) {
                    return;
                }
                Ext.fly(sourceElement).setStyle('opacity', '.2');

                var dragProxyElement = me.createDragProxy(sourceElement);

                return {
                    ddel: dragProxyElement,
                    sourceElement: sourceElement,
                    repairXY: Ext.fly(sourceElement).getXY()
                };
            },

            onMouseUp: function (e) {
                Ext.fly(this.dragData.sourceElement).setStyle('opacity', '1');
            },

            getRepairXY: function () {
                return this.dragData.repairXY;
            }
        });


        var dropZone = new Ext.dd.DropZone(parentComponent.getEl(), {
            ddGroup: group,

            getTargetFromEvent: function (e) {
                return e.getTarget('.admin-sortable');
            },

            onNodeOver: function (target, dd, e, data) {
                var node = Ext.getCmp(target.id);
                if (!node) {
                    return;
                }

                if (target === data.sourceElement) {
                    return;
                }

                var nodeHeight = node.getHeight();
                var nodeYPos = node.getEl().getY();
                var mouseYPos = e.getY();
                var nodeMiddle = nodeYPos + nodeHeight / 2;

                if (mouseYPos < nodeMiddle) {
                    me.currentPos = 'above';
                    Ext.fly(target).addCls('admin-sortable-insert-helper-above');
                    Ext.fly(target).removeCls('admin-sortable-insert-helper-below');
                } else {
                    me.currentPos = 'below';
                    Ext.fly(target).addCls('admin-sortable-insert-helper-below');
                    Ext.fly(target).removeCls('admin-sortable-insert-helper-above');
                }

                return Ext.dd.DropZone.prototype.dropAllowed;
            },

            onNodeOut: function (nodeData, source, e, data) {
                Ext.fly(nodeData).removeCls('admin-sortable-insert-helper-above');
                Ext.fly(nodeData).removeCls('admin-sortable-insert-helper-below');
            },

            onNodeDrop: function (target, dd, e, data) {
                var draggedCmp = Ext.getCmp(data.sourceElement.id);
                var targetCmp = Ext.getCmp(target.id);

                draggedCmp.getEl().setStyle('opacity', 1);

                if (targetCmp) {
                    var targetCmpIndex = me.getIndexOfComponent(targetCmp);
                    var draggedCmpOrgIndex = me.getIndexOfComponent(draggedCmp);

                    if (me.currentPos === 'below') {
                        targetCmpIndex = targetCmpIndex + 1;
                        if (draggedCmpOrgIndex < targetCmpIndex) {
                            targetCmpIndex = targetCmpIndex - 1;
                        }
                    }

                    draggedCmp.getEl().highlight();

                    me.parentComponent.insert(targetCmpIndex, draggedCmp);

                    me.parentComponent.doLayout();
                }

                return true;
            }
        });
    },

    createDragProxy: function (sourceElement) {
        var me = this;
        var proxy;
        if (me.config.proxyHtml) {
            proxy = document.createElement('div');
            proxy = Ext.fly(proxy);
            proxy.setHTML(me.config.proxyHtml);
            proxy = proxy.dom;
        } else { // If no proxy is configured we'll clone the source element
            proxy = sourceElement.cloneNode(true);
        }
        proxy.id = Ext.id();

        return proxy;
    },

    getIndexOfComponent: function (cmp) {
        return this.parentComponent.items.indexOf(cmp);
    }

});