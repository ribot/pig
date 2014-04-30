var _ = require( 'lodash' ),
    Response = require( './response' );

var paths = [];

var register = function register( path, handler ) {
  paths.push( {
    path: path,
    handler: handler
  } );
};

var send = function send( path, data, callback ) {
  if ( typeof data === 'function' ) {
    callback = data;
    data = null;
  }

  var pathHandler = _.find( paths, {path: path} );

  if ( !pathHandler ) {
    return callback( 'No handler defined for path: ' + path );
  }

  var res = new Response( callback );
  pathHandler.handler( data, res );
};

module.exports.register = register;
module.exports.send = send;
