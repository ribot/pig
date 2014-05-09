/**
 * Response constructor
 */
var Response = function Response ( key ) {
  this.key = key;
  return this;
};

Response.prototype = {

  error: function error ( message ) {
    if ( typeof window != "undefined" && window.android ) {
      window.android.reply( this.key, message, null );
    }
  },

  send: function send ( response ) {
    if ( typeof window != "undefined" && window.android ) {
      window.android.reply( this.key, null, response );
    }
  }

};

// Exports
module.exports = Response;
