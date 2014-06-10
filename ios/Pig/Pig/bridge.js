(function() {
  var responseQueue = [],
      callbackIframe;

  // External methods
  window.ios = {
    // pig-javascript interface methods

    success: function( key, data ) {
      responseQueue.push( {
        method: 'success',
        key: key,
        data: data
      } );

      notifyNewCallback();
    },

    fail: function( key, code, name, message ) {
      responseQueue.push( {
        method: 'fail',
        key: key,
        code: code,
        name: name,
        message: message
      } );

      notifyNewCallback();
    },

    // Internal pig-ios methods

    _getCallbackQueue: function() {
      var queueString = JSON.stringify( responseQueue );
      responseQueue = [];

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
