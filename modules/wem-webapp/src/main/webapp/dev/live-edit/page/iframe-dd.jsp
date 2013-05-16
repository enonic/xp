<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <title>Enonic - DD iframe test</title>

  <script type="text/javascript" charset="UTF-8" src="../../../admin2/live-edit/lib/jquery-1.8.3.min.js"></script>
  <script type="text/javascript" charset="UTF-8" src="../../../admin2/live-edit/lib/jquery-ui-1.9.2.custom.min.js"></script>
  <script type="text/javascript" charset="UTF-8" src="../../../admin2/live-edit/lib/jquery.simulate.js"></script>
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

    #window {
      background-color: rgba(0, 0, 0, .5);
      padding: 10px;
      position: absolute;
      top: 10px;
      left: 10px;
      width: 500px;
      height: 404px;
    }

    .live-edit-component {
      cursor: move;
    }
  </style>
</head>
<body>

<div id="window">
  <div class="live-edit-component" style="width:163px; height:100px; background: #fefefe; border: 1px solid #000"
       data-live-edit-component-key="10001" data-live-edit-component-name="fisk" data-live-edit-component-type="part"></div>
  <div class="live-edit-component" style="width:163px; height:100px; background: #fefefe; border: 1px solid #000"
       data-live-edit-component-key="10001" data-live-edit-component-name="fisk" data-live-edit-component-type="part"></div>
  <div class="live-edit-component" style="width:163px; height:100px; background: #fefefe; border: 1px solid #000"
       data-live-edit-component-key="10001" data-live-edit-component-name="fisk" data-live-edit-component-type="part"></div>
</div>

<iframe id="live-edit-frame" src="bootstrap.jsp?edit=true"></iframe>

<script type="text/javascript">
  function getWindow() {
    return $('#window');
  }

  function getIframe() {
    return $('#live-edit-frame');
  }

  function getLiveEditJQ() {
    return getIframe()[0].contentWindow.$liveedit;
  }

  function onDragStart(event, ui) {
    getWindow().hide();
    var $liveedit = getLiveEditJQ();

    var clone = $liveedit(ui.helper.clone());

    console.log(ui.helper[0])
    clone.css('position', 'absolute');
    clone.css('top', '10px');
    clone.css('z-index', '5100000');

    $liveedit('body').append(clone);

    $liveedit(clone).draggable({
      connectToSortable: '[data-live-edit-type=region]'
    });

    $liveedit(clone).simulate('mousedown');
  }

  $(document).ready(function () {
    $('.live-edit-component').draggable({
      helper: 'clone',
      start: onDragStart
    });
    $('#live-edit-frame').droppable();

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