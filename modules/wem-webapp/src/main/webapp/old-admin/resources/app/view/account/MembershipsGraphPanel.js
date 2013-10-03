Ext.define('Admin.view.account.MembershipsGraphPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.membershipsGraphPanel',
    autoEl: {
        tag: 'div',
        cls: 'infovis-container'
    },
    layout: 'fit',
    height: 360,
    extraCls: null,

    listeners: {
        resize: function (component, adjWidth, adjHeight) {
            // TODO: Why is adjWidth always undefined on resize?
            // Get the new width manually instead.
            var newWidth = component.getWidth();
            this.onResize(component, newWidth, adjHeight);
        }
    },

    afterRender: function () {
        this.setStyles();
        this.implementAccountIcon();
        this.createRGraph();
        this.createDetails();
        this.currentWidth = this.getWidth();
    },

    createRGraph: function () {
        var me = this;
        var levelDistance = 150;

        me.graph = new $jit.RGraph({
            'injectInto': me.getEl().id + '-body',

            'withLabels': true,

            'levelDistance': levelDistance,

            'background': {
                'levelDistance': levelDistance,
                'CanvasStyles': {
                    'strokeStyle': '#eee'
                }
            },
            Node: {
                overridable: true,
                type: 'accountIcon'
            },
            Edge: {
                overridable: true,
                color: 'rgb(238, 238, 238)',
                lineWidth: 1.1,
                type: 'arrow',
                dim: 10
            },
            Navigation: {
                enable: true,
                panning: 'avoid nodes'
            },

            // Add the node's name into the label.
            // This method is called only once, on label creation.
            onCreateLabel: function (domElement, node) {
                me.onCreateLabel.call(me, domElement, node);
            },
            // Change the node's style based on its position.
            // This method is called each time a label is rendered/positioned during an animation.
            onPlaceLabel: function (domElement, node) {
                me.onPlaceLabel.call(me, domElement, node);
            }
        });
        // Set empty graph data: jit graph throws error on resize if there is no data
        me.setGraphData([
            {}
        ]);
    },

    createDetails: function () {
        var dh = Ext.DomHelper;
        var spec = {
            tag: 'div',
            cls: 'admin-graph-details',
            id: 'admin-graph-details-' + this.getEl().id
        };
        dh.insertAfter(this.getEl(), spec);
    },

    setGraphData: function (json) {
        this.graph._loaded = false;
        this.graph.loadJSON(json);
        this.graph.refresh();
        // Flag the graph as loaded so icons are not loaded when navigating the graph.
        this.graph._loaded = true;
    },

    implementAccountIcon: function () {
        var me = this;

        $jit.RGraph.Plot.NodeTypes.implement({
            'accountIcon': {
                'render': function (node, canvas) {
                    var context = canvas.getCtx();
                    var nodePosition = node.pos.getc(true);
                    var data = node.data;
                    var text = node.name;
                    var iconSize = 16;
                    var iconMarginRight = 4;
                    var leftRightPadding = 4;

                    context.font = '11px Arial';
                    var textWidth = Math.round(context.measureText(text).width);

                    var width = textWidth + (leftRightPadding * 2) + (iconSize + iconMarginRight);
                    var height = 22;
                    var x = nodePosition.x - (width / 2);
                    var y = nodePosition.y - 11;
                    var radius = 6;

                    context.strokeStyle = 'rgb(204, 204, 204)';
                    context.lineWidth = 1;
                    context.fillStyle = 'rgba(238, 238, 238, 1)';
                    context.beginPath();
                    context.moveTo(x + radius, y);
                    context.lineTo(x + width - radius, y);
                    context.quadraticCurveTo(x + width, y, x + width, y + radius);
                    context.lineTo(x + width, y + height - radius);
                    context.quadraticCurveTo(x + width, y + height, x + width - radius, y + height);
                    context.lineTo(x + radius, y + height);
                    context.quadraticCurveTo(x, y + height, x, y + height - radius);
                    context.lineTo(x, y + radius);
                    context.quadraticCurveTo(x, y, x + radius, y);
                    context.closePath();
                    context.stroke();
                    context.fill();

                    context.fillStyle = 'black';
                    context.textBaseline = 'middle';
                    context.fillText(text, (x + leftRightPadding + iconSize + iconMarginRight), (y + height / 2));

                    // Only request image the first time the so it is not requested when navigating the graph.
                    if (data.type) {
                        var image = new Image();
                        var imageX = (x + leftRightPadding);
                        var imageY = (y + 3);

                        image.src = data.image_uri;
                        if (!me.graph._loaded) {
                            image.onload = function () {
                                context.drawImage(image, imageX, imageY, iconSize, iconSize);
                            };
                        } else {
                            // Image should be cached.
                            context.drawImage(image, imageX, imageY, iconSize, iconSize);
                        }
                    }

                    node._width = width;
                    node._height = height;
                    node._x = x;
                    node._y = y;
                }
            }
        });
    },


    /**
     * @private
     */
    onCreateLabel: function (domElement, node) {
        var me = this;
        // Toggle a node selection when clicking its name. This is done by animating some
        // node styles like its dimension and the color and lineWidth of its adjacencies.
        var style = domElement.style;
        style.width = node._width + 'px';
        style.height = node._height + 'px';
        style.cursor = 'pointer';

        domElement.onclick = function () {
            me.graph.onClick(node.id, {
                hideLabels: false
            });
            me.showMembershipList.call(me, node)
        };
    },


    /**
     * @private
     */
    onPlaceLabel: function (domElement, node) {
        var style = domElement.style;
        style.display = '';

        if (node._depth === 0) {
            style.fontSize = "16px";
            style.color = "#000";
        }
        if (node._depth === 1) {
            style.fontSize = "11px";
            style.color = "#000";
        }
        else if (node._depth >= 2) {
            style.fontSize = "10px";
            style.color = "#666";
        }
        else {
        }

        var canvasScaleOffsetX = this.graph.canvas.scaleOffsetX;

        var left = parseInt(style.left);
        var top = parseInt(style.top);
        var width = domElement.offsetWidth;

        var nw = node._width * (canvasScaleOffsetX * 2) / 2 + 8;
        var nh = node._height * (canvasScaleOffsetX * 2) / 2 + 8;
        var nt = top - (nh / 2);

        style.width = nw + 'px';
        style.height = nh + 'px';
        style.left = (left - nw / 2) + 'px';
        style.top = nt + 'px';
    },

    /**
     * @private
     */
    showMembershipList: function (node) {
        var me = this;

        var html = '<h4>' + node.name + '</h4><ul>';
        var list = [];
        node.eachAdjacency(function (adj) {
            if (adj.getData('alpha')) {
                list.push('<li>' + adj.nodeTo.name + '</li>');
            }
        });
        $jit.id('admin-graph-details-' + me.getEl().id).innerHTML = html + list.join('') + '</ul>';

        // We need to manually recalculate the container's layout in order to make the list visible.
        if (me.up()) {
            // Somehow we need to wait a bit before we doLayout. Not sure why.
            Ext.Function.defer(function () {
                me.up().doLayout()
            }, 10);
        }
    },


    /**
     * @private
     */
    setStyles: function () {
        var me = this;
        me.body.setWidth(me.width);
        me.body.setHeight(me.height);
        if (me.extraCls) {
            me.body.addCls(me.extraCls);
        }
    },

    /**
     * @private
     */
    onResize: function (component, adjWidth, adjHeight) {
        // Only exectue when width is resized as the rendering of the membership list sizes the height
        if (component.currentWidth !== adjWidth) {
            component.graph.canvas.resize(adjWidth, adjHeight);
            component.currentWidth = adjWidth;
        }
    }

});
