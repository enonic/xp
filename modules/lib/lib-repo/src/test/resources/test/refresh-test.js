var assert = require('/lib/xp/testing.js');
var repoLib = require('/lib/xp/repo.js');

exports.refreshDefault = function () {

    repoLib.refresh();

};

exports.refreshAll = function () {

    repoLib.refresh({mode: 'all'});

};

exports.refreshSearch = function () {

    repoLib.refresh({mode: 'search'});

};

exports.refreshVersion = function () {

    repoLib.refresh({mode: 'version'});

};

exports.refreshBranch = function () {

    repoLib.refresh({mode: 'branch'});

};

exports.refreshCommit = function () {

    repoLib.refresh({mode: 'commit'});

};

exports.refreshInvalid = function () {

    repoLib.refresh({mode: 'stuff'});

};
