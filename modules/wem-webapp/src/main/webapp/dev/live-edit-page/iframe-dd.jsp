<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Enonic - DD iframe test</title>

    <script type="text/javascript" charset="UTF-8" src="../../admin2/live-edit/lib/jquery-1.8.3.min.js"></script>
    <script type="text/javascript" charset="UTF-8"
            src="../../admin2/live-edit/lib/jquery-ui-1.9.2.custom.min.js"></script>
    <script type="text/javascript" charset="UTF-8" src="../../admin2/live-edit/lib/jquery.simulate.js"></script>
    <style type="text/css">
        body, html {
            height: 100%;
            overflow: hidden;
        }

        body {
            margin: 0;
            padding: 0;
        }

        iframe {
            width: 100%;
            height: 100%;
            border: 0;
        }

        #shim-for-window-dragging {
            position: absolute;
            top: 0;
            right: 0;
            bottom: 0;
            left: 0;
            background-color: transparent;
        }

        #window {
            font: 15px Arial;
            background-color: rgba(0, 0, 0, .75);
            width: 570px;
            height: 415px;
            padding: 5px 0px 5px 5px;
            position: absolute;
            top: 20px;
            left: 20px;
            box-shadow: 0 5px 10px rgba(0, 0, 0, 0.2);
        }

        #window-drag-handle {
            color: white;
            padding: 10px;
            cursor: move;
        }

        .live-edit-component {
            cursor: move;
            padding: 10px;
            margin-bottom: 5px;
            margin-right: 5px;
            float: left;
        }
    </style>
</head>
<body>
<div id="shim-for-window-dragging" style="display:none"></div>
<div id="window">
    <div id="window-drag-handle">&lt;Window&gt;</div>
    <div class="live-edit-component" style="width:163px; height:100px; background: #fefefe; border: 1px solid #000"
         data-live-edit-component-key="10001" data-live-edit-component-name="fisk" data-live-edit-component-type="part">
        <img src="http://media.w3.org/2010/05/sintel/poster.png" style="width:163px; height: 100px"/>
    </div>
    <div class="live-edit-component" style="width:163px; height:100px; background: #fefefe; border: 1px solid #000"
         data-live-edit-component-key="10001" data-live-edit-component-name="fisk" data-live-edit-component-type="part">
        <img src="http://media.w3.org/2010/05/sintel/poster.png" style="width:163px; height: 100px"/>
    </div>
    <div class="live-edit-component" style="width:163px; height:100px; background: #fefefe; border: 1px solid #000"
         data-live-edit-component-key="10001" data-live-edit-component-name="fisk" data-live-edit-component-type="part">
        <img src="http://media.w3.org/2010/05/sintel/poster.png" style="width:163px; height: 100px"/>
    </div>
    <div class="live-edit-component" style="width:163px; height:100px; background: #fefefe; border: 1px solid #000"
         data-live-edit-component-key="10001" data-live-edit-component-name="fisk" data-live-edit-component-type="part">
        <img src="http://media.w3.org/2010/05/sintel/poster.png" style="width:163px; height: 100px"/>
    </div>
    <div class="live-edit-component" style="width:163px; height:100px; background: #fefefe; border: 1px solid #000"
         data-live-edit-component-key="10001" data-live-edit-component-name="fisk" data-live-edit-component-type="part">
        <img src="http://media.w3.org/2010/05/sintel/poster.png" style="width:163px; height: 100px"/>
    </div>
    <div class="live-edit-component" style="width:163px; height:100px; background: #fefefe; border: 1px solid #000"
         data-live-edit-component-key="10001" data-live-edit-component-name="fisk" data-live-edit-component-type="part">
        <img src="http://media.w3.org/2010/05/sintel/poster.png" style="width:163px; height: 100px"/>
    </div>
    <div class="live-edit-component" style="width:163px; height:100px; background: #fefefe; border: 1px solid #000"
         data-live-edit-component-key="10001" data-live-edit-component-name="fisk" data-live-edit-component-type="part">
        <img src="http://media.w3.org/2010/05/sintel/poster.png" style="width:163px; height: 100px"/>
    </div>
</div>

<iframe id="live-edit-frame" src="bootstrap.jsp?edit=true"></iframe>

<script type="text/javascript">
    /*
     TODO:
     * When clone and append dragger to the iframe document we need to give simulated mouse down event pageX/Y positions (take scroll offset into account)
     */

    function getWindow() {
        return $('#window');
    }

    function getShimForWindowDragging() {
        return $('#shim-for-window-dragging');
    }

    function getIframe() {
        return $('#live-edit-frame');
    }

    function getLiveEditJQ() {
        return getIframe()[0].contentWindow.$liveedit;
    }

    var cursorAt = { left: 5, top: 5 };
    function onDragStart(event, ui) {
        getWindow().hide();
        var $liveedit = getLiveEditJQ();

        var clone = $liveedit(ui.helper.clone());

        clone.css('position', 'absolute');
        clone.css('z-index', '5100000');

        $liveedit('body').append(clone);

        $liveedit(clone).draggable({
            connectToSortable: '[data-live-edit-type=region]',
            cursorAt: cursorAt
        });

        $liveedit(clone).simulate('mousedown');
    }

    $(document).ready(function () {
        $('.live-edit-component').draggable({
            helper: 'clone',
            start: onDragStart,
            cursorAt: cursorAt
        });
        $('#live-edit-frame').droppable();

        $('#window').draggable({
            handle: '#window-drag-handle',
            containment: 'document',
            start: function (event, ui) {
                getShimForWindowDragging().show();
            },
            stop: function (event, ui) {
                getShimForWindowDragging().hide();
            }
        });


        // TODO: Let's pretend that the frame's document is loaded ;)
        setTimeout(function () {
            var $liveedit = getLiveEditJQ();
            $liveedit(getIframe()[0].contentWindow).on('sortStop.liveEdit.component', function () {
                getWindow().show();
                $('.live-edit-component').simulate('mouseup');
            });
        }, 1000);
    });
</script>

</body>
</html>