var _ = require( 'lodash' ),
    Response = require( './response' );

var paths = [];

var reset = function reset() {
  paths = [];
};

var handle = function handle( path, handler ) {
  // Check if this path already exists
  var pathHandler = _.find( paths, {path: path} );
  if ( pathHandler ) {
    throw new Error( 'A handler for this path already exists' );
  }

  // Add the path handler
  paths.push( {
    path: path,
    handler: handler
  } );
};

var send = function send( key, path, data ) {
  if ( key instanceof Response ) {
    var res = key;
  } else {
    var res = new Response( key );
  }

  path = unescape(path);
  if (data && data.trim() !== "") {
    data = unescape(data);

    try {
      data = JSON.parse(data);
    } catch (e) {
      return res.error( 'JSON parse error: ' + e );
    }
  }

  var pathHandler = _.find( paths, {path: path} );

  if ( !pathHandler ) {
    return res.error( 'No handler defined for path: ' + path );
  }

  pathHandler.handler( data, res );

  return res;
};

// TODO: Events

module.exports.reset = reset;
module.exports.handle = handle;
module.exports.send = send;
