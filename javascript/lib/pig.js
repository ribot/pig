// Dependencies
var PigEvents = require( './event-bus' ),
    Response = require( './response' );

/**
 * Pig constructor
 */
var Pig = function Pig() {
  this.handlers = {};
};

// Extend PigEvents
Pig.prototype = new PigEvents();

/**
 * Register a handler
 */
Pig.prototype.register = function register( path, handler ) {

  if ( this.handlers.hasOwnProperty( path ) ) {
    throw new Error( 'Handler already exists for path: ' + path );
  } else {
    this.handlers[ path ] = handler;
  }

};

/**
 * Execute a handler
 */
Pig.prototype._execute = function _execute( key, path, data ) {
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
 * Resets the Pig interface
 */
Pig.prototype._reset = function _reset() {
  this.handlers = {};
  this.removeAllListeners();
};

// Exports
module.exports = Pig;
