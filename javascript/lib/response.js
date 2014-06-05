// Dependencies
var nativeInterface = require( './native-interface' );

/**
 * Response constructor
 */
var Response = function Response( key ) {
  this.key = key;
  return this;
};

Response.prototype = {

  /**
   * Native party failure callback
   */
  fail: function fail( error, code ) {

    if ( this.key != -1 ) {
      nativeInterface.fail( this.key, ( code || null ), error.name, error.message );
    }

    _preventRecurrence.call( this );

  },

  /**
   * Native party success callback
   */
  success: function success( data ) {

    if ( this.key != -1 ) {
      nativeInterface.success( this.key, JSON.stringify( data ) );
    }

    _preventRecurrence.call( this );

  }

};

/**
 * Replaces the success and fail function in this response object with a
 * function which throws and Error.
 */
function _preventRecurrence() {

  this.fail = this.success = function recurrenceHandler() {
    throw new Error( 'Cannot respond more than once using the same response object.' );
  };

}

// Exports
module.exports = Response;
