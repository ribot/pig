var _ = require( 'lodash' );

var paths = [];

var register = function register( path, handler ) {
  paths.push( {
    path: path,
    handler: handler
  } );
};

var send = function send( path, data, callback ) {
  var pathHandler = _.find( paths, {path: path} );

  if ( !pathHandler ) {
    return callback( new Error('No handler defined for path: ' + path) );
  }

  var res = {
    error: function( error ) {
      callback( error );
    },
    send: function( response ) {
      callback( null, response );
    }
  };

  pathHandler.handler( data, res );
};

module.exports.register = register;
module.exports.send = send;
