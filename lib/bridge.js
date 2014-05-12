// Dependencies
var Response = require( './response' );

// Handlers hash
var handlers = {};

/**
 * Resets the handlers hash
 */
var reset = function reset () {
  handlers = {};
};

/**
 * Register handler
 */
var register = function register ( path, handler ) {

  if ( handlers.hasOwnProperty( path ) ) {
    throw new Error( 'Handler already exists for path: ' + path );
  } else {
    handlers[ path ] = handler;
  }

};

/**
 * Execute handler
 */
var execute = function execute ( key, path, data ) {
  var response = ( key instanceof Response ? key : new Response( key ) );

  path = unescape( path );

  if ( data && data.trim() !== "" ) {
    data = unescape( data );
    try {
      data = JSON.parse( data );
    } catch ( error ) {
      return response.fail( error );
    }
  }

  if ( handlers[ path ] ) {
    handlers[ path ]( data, response );
    return response;
  } else {
    return response.fail( new Error( 'No handler defined for path: ' + path ) );
  }

};

// Exports
module.exports.register = register;
module.exports.execute = execute;
module.exports.reset = reset;
