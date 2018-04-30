/*global should:false*/
process.env.UNIT_TEST = "test";

const chai = require('chai');
const chaiHttp = require('chai-http');
const server = require('../app');
const should = chai.should();
const Registeree = require('../models/registeree');

chai.use(chaiHttp);
//Our parent block
describe('Registerees', () => {
  beforeEach((done) => { //Before each test we empty the database
    Registeree.remove({}, () => {
      done();
    });
  });
  /*
  * Test the /GET route
  */
  describe('GET /registerees', () => {
    it('it should GET all the registerees', (done) => {
      chai.request(server)
        .get('/registerees')
        .end((err, res) => {
          res.should.have.status(200);
          res.body.should.be.a('array');
          res.body.length.should.be.eql(0);
          done();
        });
    });
  });

  describe('GET /registerees/info/:registereeId', () => {
    it('it should GET info for registeree', (done) => {
      let addRegisteree = new Registeree({
        registereeId: "R1",
        calories: 123,
        steps: 1000
      });
      addRegisteree.save((err) =>{
        if (err) {
          done();
          throw err;
        }
        chai.request(server)
          .get('/registerees/info/R1')
          .end((err, res) => {
            res.should.have.status(200);
            res.body.should.be.a('object');
            res.body.should.have.property('registereeId');
            res.body.should.have.property('registereeId').eql('R1');
            res.body.should.have.property('calories');
            res.body.should.have.property('steps');
            done();
          });
      });
    });
  });

  describe('GET /registerees/totalSteps', () => {
    it('it should GET all the steps walked by registerees', (done) => {
      let addRegisteree = new Registeree({
        registereeId: "R1",
        calories: 123,
        steps: 1000
      });
      addRegisteree.save((err) => {
        if (err) {
          done();
          throw err;
        }
        chai.request(server)
          .get('/registerees/totalSteps')
          .end((err, res) => {
            res.should.have.status(200);
            res.body.should.be.a('array');
            res.body[0].should.be.a('object');
            res.body[0].should.have.property('_id');
            res.body[0].should.have.property('count');
            res.body[0].should.have.property('count').eql(1000);
            done();
          });
      });
    });
  });

  describe('GET /registerees/totalCalories', () => {
    it('it should GET all the calories by registerees', (done) => {
      let addRegisteree = new Registeree({
        registereeId: "R1",
        calories: 123,
        steps: 1000
      });
      addRegisteree.save((err) => {
        if (err) {
          done();
          throw err;
        }
        chai.request(server)
          .get('/registerees/totalCalories')
          .end((err, res) => {
            res.should.have.status(200);
            res.body.should.be.a('array');
            res.body[0].should.be.a('object');
            res.body[0].should.have.property('_id');
            res.body[0].should.have.property('count');
            res.body[0].should.have.property('count').eql(123);
            done();
          });
      });
    });
  });

  /*
  * Test the /POST route
  */
  describe('POST /registerees/add', () => {
    it('it should POST a registeree', (done) => {
      const registeree = {
        registereeId: "R1",
        calories: 123,
        steps: 1000
      };
      chai.request(server)
        .post('/registerees/add')
        .send(registeree)
        .end((err, res) => {
          res.should.have.status(200);
          res.text.should.be.a('String');
          res.text.should.be.eql('Saved Registeree.');
          done();
        });
    });
  });


  /*
  * Test the /update route
  */
  describe('POST /registerees/update/:registereeId/steps/:steps', () => {
    it('it should POST a registeree', (done) => {
      const addRegisteree = new Registeree({
        registereeId: "R1",
        calories: 123,
        steps: 1000
      });
      addRegisteree.save((err) => {
        if (err) {
          done();
          throw err;
        }
        chai.request(server)
          .post('/registerees/update/R1/steps/100')
          .end((err, res) => {
            res.should.have.status(200);
            res.text.should.be.a('String');
            res.text.should.be.eql('Update Registeree R1\'s steps.');
            done();
          });
      });
    });
  });


  /*
  * Test the /update route
  */
  describe('POST /registerees/update/:registereeId/calories/:calories', () => {
    it('it should POST a registeree', (done) => {
      const addRegisteree = new Registeree({
        registereeId: "R1",
        calories: 123,
        steps: 1000
      });
      addRegisteree.save((err) => {
        if (err) {
          done();
          throw err;
        }
        chai.request(server)
          .post('/registerees/update/R1/calories/100')
          .end((err, res) => {
            res.should.have.status(200);
            res.text.should.be.a('String');
            res.text.should.be.eql('Update Registeree R1\'s calories.');
            done();
          });
      });
    });
  });

});