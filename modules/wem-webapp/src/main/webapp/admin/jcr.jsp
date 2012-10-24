<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8"/>
  <title>Enonic WEM - JCR Browser</title>
  <link rel="stylesheet" type="text/css" href="resources/lib/bootstrap/css/bootstrap.min.css">
  <style type="text/css">
    .treepanel {
      background: none repeat scroll 0 0 white;
      float: left;
      margin: 0;
      overflow: auto;
      width: 400px;
      height: 600px;
      overflow:auto; -ms-overflow-x:hidden; overflow-x:hidden;
      background-color: #FFFFFF;
      border: 1px solid #DDDDDD;
      border-radius: 4px 4px 4px 4px;
    }

    .bs-docs-example:after {
      background-color: #F5F5F5;
      border: 1px solid #DDDDDD;
      border-radius: 4px 0 4px 0;
      color: #9DA0A4;
      content: "Properties";
      font-size: 12px;
      font-weight: bold;
      left: -1px;
      padding: 3px 7px;
      position: absolute;
      top: -1px;
    }
    .bs-docs-example {
      background-color: #FFFFFF;
      border: 1px solid #DDDDDD;
      border-radius: 4px 4px 4px 4px;
      margin: 15px 0;
      padding: 39px 19px 14px;
      position: relative;
    }
  </style>
  <script type="text/javascript" src="../dev/live-edit/app/lib/jquery-1.8.0.min.js"></script>
  <script type="text/javascript" src="resources/lib/bootstrap/js/bootstrap.min.js"></script>
  <script type="text/javascript" src="resources/lib/jstree/jquery.jstree.js"></script>
  <script>
    var allNodesPath = [];
    var pathToNodeId = function pathToNodeId(path) {
      return 'node' + (path.join('_') || '_').
          replace(':','_', 'gi').
          replace('(','_', 'gi').
          replace(')','_', 'gi').
          replace(' ','_', 'gi');
    };
    var jcrNodesToJstree = function jcrNodesToJstree(node, openLevel, currLevel, path) {
      var dataNode = {data: {}, attr: {}, children: []};
      openLevel = openLevel || 0;
      currLevel = currLevel || 0;
      path = path ? path.slice(0) : [];
      path.push(node.name);

      dataNode.data.title = node.name;
      dataNode.data.icon = 'folder';
      dataNode.attr = {
        "properties": JSON.stringify(node.properties),
        "name": node.name,
        "path": JSON.stringify(path),
        "id": pathToNodeId(path)
      };
      dataNode.metadata = node.properties;
      dataNode.state = (currLevel <= openLevel) || (node.nodes && node.nodes.length === 0) ? 'open' : 'closed';
      allNodesPath.push(path.join('/'));

      if (node.nodes) {
        $.each(node.nodes, function (i, val) {
          dataNode.children.push(jcrNodesToJstree(val, openLevel, currLevel + 1, path));
        });
      }
      return dataNode;
    };

    var nodeSelected = function nodeSelected(name, properties, path) {
      var addRow = function (id, val) {
        var td1 = $('<td>').text(id);
        var td2 = $('<td>').text(val);
        var tr = $('<tr>').append(td1).append(td2);
        $('#nodeProperties tbody').append(tr);
      };

      $('#nodeProperties tbody').empty();
      addRow('Name', name);
      $.each(properties, function (k, val) {
        addRow(k, JSON.stringify(val));
      });

      $('#pathSelector').val(path.join('/'));
      // path breadcrumb
      var breadcrumb = $('#nodePath'), pathLength = path.length;
      var partialPath = [];
      breadcrumb.empty();
      if (pathLength === 1) {
        breadcrumb.append($('<li><span class="divider">/</span></li>'));
      }
      $.each(path, function (i, val) {
        if (i < pathLength - 1) {
          partialPath.push(val);
          var pathPart = $('<li><a href="#'+partialPath.join('/')+'">' + val + '</a> <span class="divider">/</span></li>');
          pathPart.on('click', {'path': partialPath.slice(0)}, function (e) {
            $("#jcrtree").jstree("deselect_node");
            $("#jcrtree").jstree("select_node",'#'+pathToNodeId(e.data.path));
          });
          breadcrumb.append(pathPart);
        } else {
          breadcrumb.append($('<li>' + val + '</li>'));
        }
      });
    };

    var pathSelectorChange = function pathSelectorChange(e) {
      var pathSearch = $('#pathSelector').val();
      var pathParts = pathSearch.split('/');
      var nodeId = '#' + pathToNodeId(pathParts);
      if ($(nodeId).length > 0) {
        $("#jcrtree").jstree("deselect_node"); // deselect current selected node
        $("#jcrtree").jstree("select_node", nodeId);
        $("#jcrtree").jstree("open_node", nodeId);
      }
    };

    $(document).ready(function () {
      $.getJSON('rest/jsonrpc/jcr_get', function (data) {
        var nodeRoot = data.result.nodes[0];
        nodeRoot = jcrNodesToJstree(nodeRoot, 2);

        $("#jcrtree").jstree({
          core: {
            "animation": 50
          },
          "json_data": {
            "data": function (node, callback) {
              if (node === -1) {
                callback([nodeRoot]); // root node
              } else {
                var path = JSON.parse(node.attr("path"));
                $.getJSON('rest/jsonrpc/jcr_get', {'path': path.join('/'), 'depth': 1}, function (data) {
                  var node = data.result.nodes[0];
                  path.pop();
                  node = jcrNodesToJstree(node, 0, 0, path);
                  callback(node.children);
                });
              }
            },
            async: true
          },
          "themes": {
            "theme": "classic",
            "dots": true,
            "icons": true
          },
          "plugins": [ "themes", "json_data", "ui" ]
        }).bind("select_node.jstree", function (e, data) {
              var properties = JSON.parse(data.rslt.obj.attr("properties")),
                  name = data.rslt.obj.attr("name"),
                  path = JSON.parse(data.rslt.obj.attr("path"));
              nodeSelected(name, properties, path);
            });

      }); // getJson

      $('#pathSelector').typeahead({source: allNodesPath});
      $('#pathSelector').on('change', pathSelectorChange);
    }); // doc ready
  </script>
</head>
<body>

<div class="container-fluid">
  <section id="global">
    <div class="page-header">
      <h2>Enonic WEM - JCR Browser</h2>
    </div>
  </section>

  <div class="row-fluid span11">

    <div class="span4">
      <div id="jcrtree" class="treepanel"></div>
    </div>

    <div class="span7">
      <input type="text" id="pathSelector" data-provide="typeahead" class="span7" value="/">
      <ul class="breadcrumb" id="nodePath">
        <li><span class="divider">-</span></li>
      </ul>
      <div class="bs-docs-example">
        <table class="table table-striped" id="nodeProperties">
          <thead>
          <tr>
            <th width="20%"><nobr>Property Name</nobr></th>
            <th>Value</th>
          </tr>
          </thead>
          <tbody>
          <tr>
            <td>-</td>
            <td>-</td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</div>


</body>
</html>


