var sinon = require( 'sinon' ),
    assert = require( 'assert' ),
    Pig = require( '../index' ),
    Response = require( '../lib/response' );

var pig = new Pig();

describe( 'Data out', function() {

  beforeEach( function () {
    pig._reset();

    window = {
      android: {
        fail: function() {},
        success: function() {},
        event: function() {}
      }
    };
  } );


  it( 'should call the success callback with a string', function () {
    var dummyResponse = new Response( 'dummy-key' ),
        responseSuccessSpy = sinon.spy( dummyResponse, 'success' ),
        dummyData = 'data goes in';

    // Setup the handler
    pig.register( 'event', function ( data, response ) {
      response.success( dummyData );
    } );

    // Send the request and spy on the success callback
    pig._execute( dummyResponse, 'event' );
    assert( responseSuccessSpy.calledWith( dummyData ) );

  } );


  it( 'should call the success callback with a number', function () {
    var dummyResponse = new Response( 'dummy-key' ),
        responseSuccessSpy = sinon.spy( dummyResponse, 'success' ),
        dummyData = 100;

    // Setup the handler
    pig.register( 'event', function ( data, response ) {
      response.success( dummyData );
    } );

    // Send the request and spy on the success callback
    pig._execute( dummyResponse, 'event' );
    assert( responseSuccessSpy.calledWith( dummyData ) );

  } );

  it( 'should call the success callback with a boolean', function () {
    var dummyResponse = new Response( 'dummy-key' ),
        responseSuccessSpy = sinon.spy( dummyResponse, 'success' ),
        dummyData = true;

    // Setup the handler
    pig.register( 'event', function ( data, response ) {
      response.success( dummyData );
    } );

    // Send the request and spy on the success callback
    pig._execute( dummyResponse, 'event' );
    assert( responseSuccessSpy.calledWith( dummyData ) );

  } );


  it( 'should call the success callback with an object', function () {
    var dummyResponse = new Response( 'dummy-key' ),
        responseSuccessSpy = sinon.spy( dummyResponse, 'success' ),
        dummyData = {
          name: 'Big Jeff'
        };

    // Setup the handler
    pig.register( 'event', function ( data, response ) {
      response.success( dummyData );
    } );

    // Send the request and spy on the success callback
    pig._execute( dummyResponse, 'event' );
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
    pig.register( 'event', function ( data, response ) {
      response.success( dummyData );
    } );

    // Send the request and spy on the success callback
    pig._execute( dummyResponse, 'event' );
    assert( responseSuccessSpy.calledWith( dummyData ) );

  } );

} );