var clusterLib = require('/lib/xp/cluster');
var t = require('/lib/xp/testing');

var initialized = false;
var initializeRepo = function () {
    initialized = true;
};

// BEGIN 
// Initialize data only on the master node
if (clusterLib.isMaster()) {

    initializeRepo();

}
// END

t.assertTrue(initialized);
