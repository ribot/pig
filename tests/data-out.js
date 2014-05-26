var sinon = require( 'sinon' ),
    assert = require( 'assert' ),
    Piggie = require( '../index' ),
    Response = require( '../lib/response' );

var piggie = new Piggie();

describe( 'Data out', function() {

  beforeEach( function () {
    piggie._reset();

    window = {
      android: {
        fail: function() {console.log(arguments)},
        success: function() {console.log(arguments)},
        event: function() {console.log(arguments)}
      }
    };
  } );


  it( 'should call the success callback with a string', function () {
    var dummyResponse = new Response( 'dummy-key' ),
        responseSuccessSpy = sinon.spy( dummyResponse, 'success' ),
        dummyData = 'data goes in';

    // Setup the handler
    piggie.register( 'event', function ( data, response ) {
      response.success( dummyData );
    } );

    // Send the request and spy on the success callback
    piggie._execute( dummyResponse, 'event' );
    assert( responseSuccessSpy.calledWith( dummyData ) );

  } );


  it( 'should call the success callback with a number', function () {
    var dummyResponse = new Response( 'dummy-key' ),
        responseSuccessSpy = sinon.spy( dummyResponse, 'success' ),
        dummyData = 100;

    // Setup the handler
    piggie.register( 'event', function ( data, response ) {
      response.success( dummyData );
    } );

    // Send the request and spy on the success callback
    piggie._execute( dummyResponse, 'event' );
    assert( responseSuccessSpy.calledWith( dummyData ) );

  } );

  it( 'should call the success callback with a boolean', function () {
    var dummyResponse = new Response( 'dummy-key' ),
        responseSuccessSpy = sinon.spy( dummyResponse, 'success' ),
        dummyData = true;

    // Setup the handler
    piggie.register( 'event', function ( data, response ) {
      response.success( dummyData );
    } );

    // Send the request and spy on the success callback
    piggie._execute( dummyResponse, 'event' );
    assert( responseSuccessSpy.calledWith( dummyData ) );

  } );


  it( 'should call the success callback with an object', function () {
    var dummyResponse = new Response( 'dummy-key' ),
        responseSuccessSpy = sinon.spy( dummyResponse, 'success' ),
        dummyData = {
          name: 'Big Jeff'
        };

    // Setup the handler
    piggie.register( 'event', function ( data, response ) {
      response.success( dummyData );
    } );

    // Send the request and spy on the success callback
    piggie._execute( dummyResponse, 'event' );
    assert( responseSuccessSpy.calledWith( dummyData ) );

  } );


  it( 'should call the success callback with an array', function () {
    var dummyResponse = new Response( 'dummy-key' ),
        responseSuccessSpy = sinon.spy( dummyResponse, 'success' ),
        dummyData = [
          'Big Jeff',
          'Bobbert'
        ];

    // Setup the handler
    piggie.register( 'event', function ( data, response ) {
      response.success( dummyData );
    } );

    // Send the request and spy on the success callback
    piggie._execute( dummyResponse, 'event' );
    assert( responseSuccessSpy.calledWith( dummyData ) );

  } );

} );
