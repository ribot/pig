var _ = require( 'lodash' ),
    Response = require( './response' );

var paths = [];

var handle = function handle( path, handler ) {
  // TODO: Error if a handler for that path already exists
  paths.push( {
    path: path,
    handler: handler
  } );
};

var send = function send( key, path, data ) {
  var res = new Response( key );

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
};

// TODO: Events

module.exports.handle = handle;
module.exports.send = send;
