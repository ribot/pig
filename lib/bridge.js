var _ = require( 'lodash' ),
    Response = require( './response' );

var paths = [];

var register = function register( path, handler ) {
  paths.push( {
    path: path,
    handler: handler
  } );
};

var send = function send( key, path, data ) {
  var res = new Response( key );
  var pathHandler = _.find( paths, {path: path} );

  if ( !pathHandler ) {
    return res.error( 'No handler defined for path: ' + path );
  }

  pathHandler.handler( data, res );
};

module.exports.register = register;
module.exports.send = send;
