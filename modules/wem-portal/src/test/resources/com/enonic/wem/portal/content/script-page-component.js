try {
  request.getEntity( null )
}
catch (e) {
  // ignore
}

var path = request.getPath();

console.log( path );