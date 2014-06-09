(function() {
  var requestQueue = [],
      callbackIframe;

  // External methods
  window.ios = {
    // pig-javascript interface methods

    success: function( callbackId, data ) {
      requestQueue.push( {
        method: 'success',
        callbackId: callbackId,
        data: data
      } );

      notifyNewCallback();
    },

    fail: function( callbackId, code, name, message ) {
      requestQueue.push( {
        method: 'fail',
        callbackId: callbackId,
        code: code,
        name: name,
        message: message
      } );

      notifyNewCallback();
    },

    // Internal pig-ios methods

    _getCallbackQueue: function() {
      var queueString = JSON.stringify( requestQueue );
      requestQueue = [];

      return queueString;
    }
  };

  function notifyNewCallback() {
    if ( !callbackIframe ) {
      createCallbackIframe();
    }

    callbackIframe.src = 'pig://callback';
  }

  function createCallbackIframe() {
    callbackIframe = document.createElement( 'iframe' );
    callbackIframe.style.display = 'none';
    document.documentElement.appendChild( callbackIframe );
  }
})();
