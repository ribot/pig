// Dependencies
var PiggieEvents = require( './event-bus' ),
    Response = require( './response' );

/**
 * Piggie constructor
 */
var Piggie = function Piggie () {
  this.handlers = {};
};

// Extend PiggieEvents
Piggie.prototype = new PiggieEvents();

/**
 * Register handler
 */
Piggie.prototype.register = function register ( path, handler ) {

  if ( this.handlers.hasOwnProperty( path ) ) {
    throw new Error( 'Handler already exists for path: ' + path );
  } else {
    this.handlers[ path ] = handler;
  }

};

/**
 * Execute handler
 */
Piggie.prototype._execute = function _execute ( key, path, data ) {
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

  if ( this.handlers[ path ] ) {
    this.handlers[ path ]( data, response );
    return response;
  } else {
    return response.fail( new Error( 'No handler defined for path: ' + path ) );
  }

};

/**
 * Resets the Piggie interface
 */
Piggie.prototype._reset = function _reset () {
  this.handlers = {};
  this.removeAllListeners();
};

// Exports
module.exports = Piggie;
