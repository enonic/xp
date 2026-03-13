var clusterLib = require('/lib/xp/cluster');
var t = require('/lib/xp/testing');

var initialized = false;
var initializeRepo = function () {
    initialized = true;
};

// BEGIN
// Initialize data only on the leader node (Hazelcast-based)
if (clusterLib.isLeader()) {

    initializeRepo();

}
// END

t.assertTrue(initialized);
