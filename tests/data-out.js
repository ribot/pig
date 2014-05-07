var sinon = require( 'sinon' )
    assert = require( 'assert' ),
    piggie = require( '../index' ),
    Response = process.env.PIGGIE_COVERAGE ? require( '../lib-cov/response' ) : require( '../lib/response' );

describe( 'Data Out', function() {
  beforeEach( function() {
    piggie.reset();
  } );


  it( 'can respond with a string', function() {
    // Setup the handler
    piggie.handle( 'event', function( data, res ) {
      res.send( 'String' );
    } );

    // Send the request and spy on the response
    var res = new Response( '1' );
    var spy = sinon.spy( res, 'send');
    piggie.send( res, 'event' );
    assert( spy.calledWith( 'String' ) );
  } );


  it( 'can respond with a number', function() {
    // Setup the handler
    piggie.handle( 'event', function( data, res ) {
      res.send( 123456789 );
    } );

    // Send the request and spy on the response
    var res = new Response( '1' );
    var spy = sinon.spy( res, 'send');
    piggie.send( res, 'event' );
    assert( spy.calledWith( 123456789 ) );
  } );


  it( 'can respond with a boolean', function() {
    // Setup the handler
    piggie.handle( 'event', function( data, res ) {
      res.send( false );
    } );

    // Send the request and spy on the response
    var res = new Response( '1' );
    var spy = sinon.spy( res, 'send');
    piggie.send( res, 'event' );
    assert( spy.calledWith( false ) );
  } );


  it( 'can respond with a JSON object', function() {
    // Setup the handler
    piggie.handle( 'event', function( data, res ) {
      res.send( { name: "Big Jeff" } );
    } );

    // Send the request and spy on the response
    var res = new Response( '1' );
    var spy = sinon.spy( res, 'send');
    piggie.send( res, 'event' );
    assert( spy.calledWith( { name: "Big Jeff" } ) );
  } );


  it( 'can respond with a JSON array', function() {
    // Setup the handler
    piggie.handle( 'event', function( data, res ) {
      res.send( [ "Big Jeff", "Bobbert" ] );
    } );

    // Send the request and spy on the response
    var res = new Response( '1' );
    var spy = sinon.spy( res, 'send');
    piggie.send( res, 'event' );
    assert( spy.calledWith( [ "Big Jeff", "Bobbert" ] ) );
  } );
} );
