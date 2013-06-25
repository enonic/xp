/**
 * TODO: Controller? :)
 */
Ext.define('Admin.view.ScreenResizer', {
    extend: 'Ext.container.Container',
    alias: 'widget.screenResizer',
    modal: false,
    shadow: false,
    border: false,
    overflowX: 'hidden',
    cls: 'screen-resizer-ct-main',

    toolbarCt: undefined,
    breakpointCmp: undefined,
    resizerCmp: undefined,
    rulerCmp: undefined,

    initComponent: function () {
        this.toolbarCt = this.createToolbarCt();
        this.breakpointCmp = this.createBreakpointCmp();
        this.resizerCmp = this.createResizerCmp();
        this.rulerCmp = this.createRulerCmp();
        this.items = [
            this.toolbarCt,
            this.breakpointCmp,
            this.resizerCmp,
            this.rulerCmp
        ];
        this.callParent(arguments);
    },

    createToolbarCt: function () {
        var me = this;
        return new Ext.container.Container({
            cls: 'screen-resizer-toolbar',
            items: [
                {
                    xtype: 'button',
                    cls: 'screen-resizer-button',
                    text: '+',
                    handler: function () {
                        var s = new Admin.view.ScreenResizerPopup({
                            opener: me,
                            closeCallback: me.addBreakpoint
                        });
                        s.doShow();
                    }
                }
            ]
        });
    },

    createBreakpointCmp: function () {
        var me = this;
        return new Ext.Component({
            cls: 'screen-resizer-breakpoint-cmp',
            listeners: {
                render: function (cmp) {
                    cmp.getEl().on('click', function (event, targetDom) {
                        if (me.isClickTargetBreakpoint(targetDom)) {
                            var w = Ext.fly(targetDom).getWidth();
                            me.resizerCmp.getEl().setWidth(w);
                            me.getLiveEditIFrameEl().setWidth(w);
                            me.setWidthText();
                        }
                    });
                    cmp.getEl().on('contextmenu', function (event, targetDom) {
                        event.preventDefault();
                        if (me.isClickTargetBreakpoint(targetDom)) {
                            Ext.fly(targetDom).remove();
                        }
                    });
                }
            }
        });
    },

    createResizerCmp: function () {
        var me = this;
        return new Ext.Component({
            cls: 'screen-resizer-cmp',
            html: '<div class="screen-resizer"><span class="screen-resizer-text"></span><span class="screen-resizer-handle"></span></div>',
            resizable: {
                width: Ext.getBody().getWidth() - 3,
                dynamic: true,
                handles: 'e',
                transparent: true,
                listeners: {
                    resizedrag: function (resizer, newWidth) {
                        me.getLiveEditIFrameEl().setWidth(newWidth);
                        me.setWidthText();
                    },
                    resize: function (resizer, newWidth) {
                        // me.getLiveEditIFrameEl().setWidth(newWidth);
                        me.setWidthText();
                    }
                }
            }
        });
    },

    createRulerCmp: function () {
        var me = this;
        return new Ext.Component({
            cls: 'screen-resizer-ruler-cmp',
            autoEl: {
                tag: 'canvas',
                height: '18',
                width: '5000'
            },
            listeners: {
                render: function (cmp) {
                    me.renderRulerCanvas(cmp.getEl().dom);
                }
            }
        });
    },

    renderRulerCanvas: function (canvasDom) {
        var CANVAS_WIDTH = canvasDom.width,
            CANVAS_HEIGHT = canvasDom.height,
            BACKGROUND_COLOR = '#38383a',
            LINE_COLOR = '#767676',
            TEXT_COLOR = '#767676';

        var ctx = canvasDom.getContext('2d');

        ctx.fillStyle = BACKGROUND_COLOR;
        ctx.fillRect (0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

        // Lines
        var moveToX,
            moveToY,
            lineToX,
            lineToY;
        for (var i = 0; i < CANVAS_WIDTH; i++) {
            moveToX = lineToX = i * 10;
            moveToY = 15;
            lineToY = 20;

            if (i % 5 == 0) {
                moveToY = 0;
                ctx.fillStyle = TEXT_COLOR;
                ctx.font = '10px Arial';
                ctx.fillText(moveToX, (moveToX + 5), 11);
            }
            ctx.beginPath();
            ctx.moveTo(moveToX, moveToY);
            ctx.lineTo(lineToX, lineToY);
            ctx.strokeStyle = LINE_COLOR;
            ctx.stroke();
        }
    },

    setWidthText: function () {
        var resizerEl = this.resizerCmp.getEl();
        resizerEl.down('.screen-resizer-text').dom.innerHTML = resizerEl.getWidth() + 'px';
    },

    addBreakpoint: function (name, width, color) {
        var breakpointEl = this.breakpointCmp.getEl();
        var breakpointBarSpec = {
            tag: 'div',
            cls: 'screen-resizer-breakpoint',
            style: 'width: ' + width + 'px; background-color: ' + color,
            html: name,
            title: name + ' (' + width + 'px)'
        };
        Ext.DomHelper.append(breakpointEl, breakpointBarSpec);

        this.reStackBreakpointZIndexes();
    },

    reStackBreakpointZIndexes: function () {
        var breakpointEl = this.breakpointCmp.getEl(),
            dq = Ext.DomQuery;

        var breakpoints = dq.select('.screen-resizer-breakpoint', breakpointEl.dom),
            newStack = [];
        Ext.Array.forEach(breakpoints, function (breakpoint, i) {
            newStack.push([breakpoint, parseInt(breakpoint.style.width, 10)]);
        });

        newStack = newStack.sort(function (a, b) {
            return a[1] - b[1];
        }).reverse();

        Ext.Array.forEach(newStack, function (s, i) {
            s[0].style.zIndex = i;
        });
    },

    isClickTargetBreakpoint: function (targetDom) {
        return targetDom.className && targetDom.className == 'screen-resizer-breakpoint';
    },

    getLiveEditIFrameEl: function () {
        return Ext.get('live-edit-iframe');
    }

});